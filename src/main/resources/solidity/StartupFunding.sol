// SPDX-License-Identifier: MIT
pragma solidity ^0.8.21;

import "@openzeppelin/contracts@4.9.0/security/ReentrancyGuard.sol";
import "@openzeppelin/contracts@4.9.0/access/Ownable.sol";
import "./PickenToken.sol";

contract StartupFunding is ReentrancyGuard, Ownable {
    PickenToken public pickenToken;

    enum CampaignStatus {
        ACTIVE,     // 투자 진행중
        SUCCESSFUL, // 목표금액 달성
        FAILED,     // 기간 종료 but 목표금액 미달성
        CANCELLED   // 관리자에 의해 취소됨
    }

    struct StartupCampaign {
        string name;                // 스타트업 이름
        string description;         // 설명
        address payable startupWallet; // 스타트업 지갑 주소
        uint256 targetAmount;       // 목표 금액
        uint256 raisedAmount;       // 현재 모인 금액
        uint256 startTime;          // 캠페인 시작 시간
        uint256 endTime;            // 캠페인 종료 시간
        CampaignStatus status;      // 캠페인 상태
        mapping(address => uint256) investments; // 투자자별 투자 금액
    }

    mapping(uint256 => uint256) public campaignBalances;
    mapping(uint256 => StartupCampaign) public campaigns;
    uint256 public campaignCount;

    event CampaignCreated(
        uint256 indexed campaignId,
        string name,
        address startupWallet,
        uint256 targetAmount,
        uint256 startTime,
        uint256 endTime
    );

    event InvestmentMade(
        uint256 indexed campaignId,
        address indexed investor,
        uint256 amount,
        uint256 totalRaised
    );

    event CampaignCompleted(
        uint256 indexed campaignId,
        CampaignStatus status,
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

    event RefundProcessed(
        uint256 indexed campaignId,
        address indexed investor,
        uint256 amount
    );

    constructor(address _pickenToken) {
        pickenToken = PickenToken(_pickenToken);
    }

    function createCampaign(
        string memory _name,
        string memory _description,
        address payable _startupWallet,
        uint256 _targetAmount,
        uint256 _duration
    ) external onlyOwner returns (uint256) {
        require(_targetAmount > 0, "Target amount must be positive");
        require(_startupWallet != address(0), "Invalid startup wallet");
        require(_duration > 0, "Duration must be positive");

        uint256 campaignId = campaignCount++;
        StartupCampaign storage campaign = campaigns[campaignId];

        uint256 startTime = block.timestamp;
        uint256 endTime = startTime + _duration;

        campaign.name = _name;
        campaign.description = _description;
        campaign.startupWallet = _startupWallet;
        campaign.targetAmount = _targetAmount;
        campaign.startTime = startTime;
        campaign.endTime = endTime;
        campaign.status = CampaignStatus.ACTIVE;

        emit CampaignCreated(
            campaignId,
            _name,
            _startupWallet,
            _targetAmount,
            startTime,
            endTime
        );

        return campaignId;
    }

    function invest(uint256 _campaignId, uint256 _amount) external nonReentrant {
        StartupCampaign storage campaign = campaigns[_campaignId];

        require(campaign.status == CampaignStatus.ACTIVE, "Campaign is not active");
        require(block.timestamp >= campaign.startTime, "Campaign not started");
        require(block.timestamp <= campaign.endTime, "Campaign ended");
        require(_amount > 0, "Amount must be greater than 0");

        uint256 newTotal = campaign.raisedAmount + _amount;
        require(newTotal <= campaign.targetAmount, "Would exceed target amount");

        uint256 allowance = pickenToken.allowance(msg.sender, address(this));
        require(allowance >= _amount, "Insufficient token allowance");

        require(
            pickenToken.transferFrom(msg.sender, address(this), _amount),
            "Token transfer failed"
        );

        campaignBalances[_campaignId] += _amount;
        campaign.investments[msg.sender] += _amount;
        campaign.raisedAmount += _amount;

        emit TokensHeld(_campaignId, _amount);
        emit InvestmentMade(
            _campaignId,
            msg.sender,
            _amount,
            campaign.raisedAmount
        );

        if (campaign.raisedAmount == campaign.targetAmount) {
            _completeCampaign(_campaignId);
        }
    }

    function _completeCampaign(uint256 _campaignId) internal {
        StartupCampaign storage campaign = campaigns[_campaignId];
        require(campaign.status == CampaignStatus.ACTIVE, "Campaign not active");
        require(campaign.raisedAmount == campaign.targetAmount, "Target not reached");

        campaign.status = CampaignStatus.SUCCESSFUL;

        uint256 amountToTransfer = campaignBalances[_campaignId];
        campaignBalances[_campaignId] = 0;

        require(
            pickenToken.transfer(campaign.startupWallet, amountToTransfer),
            "Failed to transfer funds to startup"
        );

        emit TokensTransferred(_campaignId, campaign.startupWallet, amountToTransfer);
        emit CampaignCompleted(_campaignId, CampaignStatus.SUCCESSFUL, amountToTransfer);
    }

    function finalizeCampaign(uint256 _campaignId) external {
        StartupCampaign storage campaign = campaigns[_campaignId];
        require(campaign.status == CampaignStatus.ACTIVE, "Campaign not active");
        require(block.timestamp > campaign.endTime, "Campaign still active");

        if (campaign.raisedAmount < campaign.targetAmount) {
            campaign.status = CampaignStatus.FAILED;
            emit CampaignCompleted(_campaignId, CampaignStatus.FAILED, campaign.raisedAmount);
        }
    }

    function refund(uint256 _campaignId) external nonReentrant {
        StartupCampaign storage campaign = campaigns[_campaignId];
        require(campaign.status == CampaignStatus.FAILED ||
        campaign.status == CampaignStatus.CANCELLED,
            "Campaign not failed or cancelled");

        uint256 amount = campaign.investments[msg.sender];
        require(amount > 0, "No investment to refund");

        campaign.investments[msg.sender] = 0;
        campaign.raisedAmount -= amount;
        campaignBalances[_campaignId] -= amount;

        require(
            pickenToken.transfer(msg.sender, amount),
            "Refund failed"
        );

        emit RefundProcessed(_campaignId, msg.sender, amount);
    }

    // Getter functions...
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

    function getCampaignStatus(uint256 _campaignId) external view returns (
        CampaignStatus status,
        uint256 startTime,
        uint256 endTime,
        uint256 timeRemaining
    ) {
        StartupCampaign storage campaign = campaigns[_campaignId];
        uint256 remaining = block.timestamp < campaign.endTime ?
            campaign.endTime - block.timestamp : 0;

        return (
            campaign.status,
            campaign.startTime,
            campaign.endTime,
            remaining
        );
    }

    function getTotalHeldTokens() external view returns (uint256) {
        return pickenToken.balanceOf(address(this));
    }

    function emergencyWithdraw(
        uint256 _campaignId,
        address _to
    ) external onlyOwner {
        StartupCampaign storage campaign = campaigns[_campaignId];
        require(_to != address(0), "Invalid address");
        require(campaign.status == CampaignStatus.ACTIVE, "Campaign not active");

        uint256 balance = campaignBalances[_campaignId];
        require(balance > 0, "No tokens to withdraw");

        campaign.status = CampaignStatus.CANCELLED;
        campaignBalances[_campaignId] = 0;

        emit CampaignCompleted(_campaignId, CampaignStatus.CANCELLED, balance);

        require(
            pickenToken.transfer(_to, balance),
            "Emergency withdrawal failed"
        );
    }
}