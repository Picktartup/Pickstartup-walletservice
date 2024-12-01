// SPDX-License-Identifier: MIT
pragma solidity ^0.8.21;

import "@openzeppelin/contracts/token/ERC20/ERC20.sol";
import "@openzeppelin/contracts/access/Ownable.sol";

/**
 * @title PickenToken
 * @dev PG 결제 시스템과 연동된 ERC20 토큰 컨트랙트
 * 결제 완료 후 해당하는 양의 토큰을 발행하는 기능을 제공
 */

contract PickenToken is ERC20, Ownable {

    // 결제 내역 매핑
    mapping(string => bool) public processedPayments;

    event TokenMinted(
        address indexed to, //토큰을 받는 주소
        uint256 amount, //발행되는 토큰의 양
        string orderId //결제 주문 번호
    );

    /**
     * @dev 컨트랙트 생성자
     * @param initialSupply 초기 발행량
     * 토큰의 이름을 "PICKEN"으로, 심볼을 "PKN"으로 설정
     * 컨트랙트 배포자에게 초기 물량을 발행
     */
    constructor(uint256 initialSupply)
    ERC20("PICKEN", "PKN")
    {
        _mint(msg.sender, initialSupply * 10**decimals());
    }

    // PG 결제 후 토큰 발행
    function mintFromPayment(
        address to,
        uint256 amount,
        string memory orderId
    ) public onlyOwner {
        // 이미 처리된 결제인지 확인
        //호출자가 컨트렉트 소유자여야 함
        require(!processedPayments[orderId], "Payment already processed");

        // 토큰 발행
        _mint(to, amount);

        // 처리된 결제 기록
        processedPayments[orderId] = true;

        emit TokenMinted(to, amount, orderId);
    }
}