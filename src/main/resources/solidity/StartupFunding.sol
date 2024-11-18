// SPDX-License-Identifier: MIT
pragma solidity ^0.8.21;

import "@openzeppelin/contracts/security/ReentrancyGuard.sol";
import "@openzeppelin/contracts/access/Ownable.sol";
import "./PickenToken.sol";

/**
 * @title StartupFunding
 * @dev 스타트업 펀딩을 위한 스마트 컨트랙트
 * PICKEN 토큰을 사용하여 투자를 진행하고 관리하는 기능 제공
 */
contract StartupFunding is ReentrancyGuard, Ownable {
    PickenToken public pickenToken;

    struct StartupCampaign {
        string name;                // 스타트업 이름
        string description;         // 설명
        address payable startupWallet; // 스타트업 지갑 주소
        uint256 targetAmount;       // 목표 금액
        uint256 raisedAmount;       // 현재 모인 금액
        bool isActive;              // 진행 중 여부
        bool isSuccessful;          // 성공 여부
        mapping(address => uint256) investments; // 투자자별 투자 금액
    }

    // 캠페인별 토큰 보관 현황을 추적하는 매핑 추가
    mapping(uint256 => uint256) public campaignBalances;
    mapping(uint256 => StartupCampaign) public campaigns;
    uint256 public campaignCount;

    // 이벤트 추가
    event CampaignCreated(
        uint256 indexed campaignId,
        string name,
        address startupWallet,
        uint256 targetAmount
    );

    event InvestmentMade(
        uint256 indexed campaignId,
        address indexed investor,
        uint256 amount,
        uint256 totalRaised
    );

    event CampaignSuccessful(
        uint256 indexed campaignId,
        uint256 totalAmount
    );

    event TokensHeld(
        uint256 indexed campaignId,
        uint256 amount
    );

    event TokensTransferred(
        uint256 indexed campaignId,
        address to,
        uint256 amount
    );

    constructor(address _pickenToken) {
        pickenToken = PickenToken(_pickenToken);
    }

    // 새로운 스타트업 캠페인 생성
    function createCampaign(
        string memory _name,
        string memory _description,
        address payable _startupWallet,
        uint256 _targetAmount
    ) external onlyOwner returns (uint256) {
        require(_targetAmount > 0, "Target amount must be positive");
        require(_startupWallet != address(0), "Invalid startup wallet");

        uint256 campaignId = campaignCount++;
        StartupCampaign storage campaign = campaigns[campaignId];

        campaign.name = _name;
        campaign.description = _description;
        campaign.startupWallet = _startupWallet;
        campaign.targetAmount = _targetAmount;
        campaign.isActive = true;
        campaign.isSuccessful = false;

        emit CampaignCreated(
            campaignId,
            _name,
            _startupWallet,
            _targetAmount
        );

        return campaignId;
    }

    // 투자하기
    function invest(uint256 _campaignId, uint256 _amount) external nonReentrant {
        StartupCampaign storage campaign = campaigns[_campaignId];

        require(campaign.isActive, "Campaign is not active");
        require(_amount > 0, "Amount must be greater than 0");

        // 투자자의 토큰을 컨트랙트로 전송
        require(
            pickenToken.transferFrom(msg.sender, address(this), _amount),
            "Token transfer failed"
        );

        // 캠페인별 토큰 보관 현황 업데이트
        campaignBalances[_campaignId] += _amount;

        // 투자 기록
        campaign.investments[msg.sender] += _amount;
        campaign.raisedAmount += _amount;

        emit TokensHeld(_campaignId, _amount);
        emit InvestmentMade(_campaignId, msg.sender, _amount, campaign.raisedAmount);

        // 목표 금액 달성 시 자동으로 스타트업에 전송
        if (campaign.raisedAmount >= campaign.targetAmount) {
            _completeCampaign(_campaignId);
        }
    }

    // 캠페인 완료 처리 (내부 함수)
    function _completeCampaign(uint256 _campaignId) internal {
        StartupCampaign storage campaign = campaigns[_campaignId];
        campaign.isActive = false;
        campaign.isSuccessful = true;

        uint256 amountToTransfer = campaignBalances[_campaignId];
        campaignBalances[_campaignId] = 0;

        // 모인 금액을 스타트업에 전송
        require(
            pickenToken.transfer(campaign.startupWallet, amountToTransfer),
            "Failed to transfer funds to startup"
        );

        emit TokensTransferred(_campaignId, campaign.startupWallet, amountToTransfer);
        emit CampaignSuccessful(_campaignId, amountToTransfer);
    }

    // 특정 투자자의 투자 금액 조회
    function getInvestmentAmount(uint256 _campaignId, address _investor)
    external
    view
    returns (uint256)
    {
        return campaigns[_campaignId].investments[_investor];
    }

    // 캠페인의 현재 토큰 보유 현황 조회
    function getCampaignBalance(uint256 _campaignId) external view returns (
        uint256 targetAmount,
        uint256 currentBalance,
        uint256 remainingAmount
    ) {
        StartupCampaign storage campaign = campaigns[_campaignId];
        uint256 remaining = campaign.targetAmount - campaign.raisedAmount;

        return (
            campaign.targetAmount,
            campaignBalances[_campaignId],
            remaining
        );
    }

    // 특정 투자자의 캠페인 참여 현황 조회
    function getInvestorStatus(
        uint256 _campaignId,
        address _investor
    ) external view returns (
        uint256 investedAmount,
        uint256 campaignTotal,
        uint256 sharePercentage
    ) {
        StartupCampaign storage campaign = campaigns[_campaignId];
        uint256 invested = campaign.investments[_investor];
        uint256 percentage = campaign.raisedAmount > 0 ?
            (invested * 100) / campaign.raisedAmount : 0;

        return (
            invested,
            campaign.raisedAmount,
            percentage
        );
    }

    // 컨트랙트의 총 토큰 보유량 확인
    function getTotalHeldTokens() external view returns (uint256) {
        return pickenToken.balanceOf(address(this));
    }

    // 긴급 상황을 위한 관리자 기능
    function emergencyWithdraw(
        uint256 _campaignId,
        address _to
    ) external onlyOwner {
        require(_to != address(0), "Invalid address");
        uint256 balance = campaignBalances[_campaignId];
        require(balance > 0, "No tokens to withdraw");

        campaignBalances[_campaignId] = 0;
        require(
            pickenToken.transfer(_to, balance),
            "Emergency withdrawal failed"
        );
    }
}