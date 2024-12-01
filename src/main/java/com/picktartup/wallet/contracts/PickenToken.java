package com.picktartup.wallet.contracts;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/hyperledger/web3j/tree/main/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.6.1.
 */
@SuppressWarnings("rawtypes")
public class PickenToken extends Contract {
    public static final String BINARY = "608060405234801561000f575f80fd5b506040516124f13803806124f183398181016040528101906100319190610382565b6040518060400160405280600681526020017f5049434b454e00000000000000000000000000000000000000000000000000008152506040518060400160405280600381526020017f504b4e000000000000000000000000000000000000000000000000000000000081525081600390816100ac91906105de565b5080600490816100bc91906105de565b5050506100db6100d061011560201b60201c565b61011c60201b60201c565b61010f336100ed6101df60201b60201c565b600a6100f99190610815565b83610104919061085f565b6101e760201b60201c565b50610973565b5f33905090565b5f60055f9054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690508160055f6101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055508173ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff167f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e060405160405180910390a35050565b5f6012905090565b5f73ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1603610255576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161024c906108fa565b60405180910390fd5b6102665f838361034160201b60201c565b8060025f8282546102779190610918565b92505081905550805f808473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f205f82825401925050819055508173ffffffffffffffffffffffffffffffffffffffff165f73ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef83604051610324919061095a565b60405180910390a361033d5f838361034660201b60201c565b5050565b505050565b505050565b5f80fd5b5f819050919050565b6103618161034f565b811461036b575f80fd5b50565b5f8151905061037c81610358565b92915050565b5f602082840312156103975761039661034b565b5b5f6103a48482850161036e565b91505092915050565b5f81519050919050565b7f4e487b71000000000000000000000000000000000000000000000000000000005f52604160045260245ffd5b7f4e487b71000000000000000000000000000000000000000000000000000000005f52602260045260245ffd5b5f600282049050600182168061042857607f821691505b60208210810361043b5761043a6103e4565b5b50919050565b5f819050815f5260205f209050919050565b5f6020601f8301049050919050565b5f82821b905092915050565b5f6008830261049d7fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff82610462565b6104a78683610462565b95508019841693508086168417925050509392505050565b5f819050919050565b5f6104e26104dd6104d88461034f565b6104bf565b61034f565b9050919050565b5f819050919050565b6104fb836104c8565b61050f610507826104e9565b84845461046e565b825550505050565b5f90565b610523610517565b61052e8184846104f2565b505050565b5b81811015610551576105465f8261051b565b600181019050610534565b5050565b601f8211156105965761056781610441565b61057084610453565b8101602085101561057f578190505b61059361058b85610453565b830182610533565b50505b505050565b5f82821c905092915050565b5f6105b65f198460080261059b565b1980831691505092915050565b5f6105ce83836105a7565b9150826002028217905092915050565b6105e7826103ad565b67ffffffffffffffff811115610600576105ff6103b7565b5b61060a8254610411565b610615828285610555565b5f60209050601f831160018114610646575f8415610634578287015190505b61063e85826105c3565b8655506106a5565b601f19841661065486610441565b5f5b8281101561067b57848901518255600182019150602085019450602081019050610656565b868310156106985784890151610694601f8916826105a7565b8355505b6001600288020188555050505b505050505050565b7f4e487b71000000000000000000000000000000000000000000000000000000005f52601160045260245ffd5b5f8160011c9050919050565b5f808291508390505b600185111561072f5780860481111561070b5761070a6106ad565b5b600185161561071a5780820291505b8081029050610728856106da565b94506106ef565b94509492505050565b5f826107475760019050610802565b81610754575f9050610802565b816001811461076a5760028114610774576107a3565b6001915050610802565b60ff841115610786576107856106ad565b5b8360020a91508482111561079d5761079c6106ad565b5b50610802565b5060208310610133831016604e8410600b84101617156107d85782820a9050838111156107d3576107d26106ad565b5b610802565b6107e584848460016106e6565b925090508184048111156107fc576107fb6106ad565b5b81810290505b9392505050565b5f60ff82169050919050565b5f61081f8261034f565b915061082a83610809565b92506108577fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff8484610738565b905092915050565b5f6108698261034f565b91506108748361034f565b92508282026108828161034f565b91508282048414831517610899576108986106ad565b5b5092915050565b5f82825260208201905092915050565b7f45524332303a206d696e7420746f20746865207a65726f2061646472657373005f82015250565b5f6108e4601f836108a0565b91506108ef826108b0565b602082019050919050565b5f6020820190508181035f830152610911816108d8565b9050919050565b5f6109228261034f565b915061092d8361034f565b9250828201905080821115610945576109446106ad565b5b92915050565b6109548161034f565b82525050565b5f60208201905061096d5f83018461094b565b92915050565b611b71806109805f395ff3fe608060405234801561000f575f80fd5b50600436106100fe575f3560e01c80638da5cb5b11610095578063a457c2d711610064578063a457c2d7146102ae578063a9059cbb146102de578063dd62ed3e1461030e578063f2fde38b1461033e576100fe565b80638da5cb5b1461022657806395d89b41146102445780639b931abf146102625780639ff14f2214610292576100fe565b8063313ce567116100d1578063313ce5671461019e57806339509351146101bc57806370a08231146101ec578063715018a61461021c576100fe565b806306fdde0314610102578063095ea7b31461012057806318160ddd1461015057806323b872dd1461016e575b5f80fd5b61010a61035a565b6040516101179190611033565b60405180910390f35b61013a600480360381019061013591906110f1565b6103ea565b6040516101479190611149565b60405180910390f35b61015861040c565b6040516101659190611171565b60405180910390f35b6101886004803603810190610183919061118a565b610415565b6040516101959190611149565b60405180910390f35b6101a6610443565b6040516101b391906111f5565b60405180910390f35b6101d660048036038101906101d191906110f1565b61044b565b6040516101e39190611149565b60405180910390f35b6102066004803603810190610201919061120e565b610481565b6040516102139190611171565b60405180910390f35b6102246104c6565b005b61022e6104d9565b60405161023b9190611248565b60405180910390f35b61024c610501565b6040516102599190611033565b60405180910390f35b61027c6004803603810190610277919061138d565b610591565b6040516102899190611149565b60405180910390f35b6102ac60048036038101906102a791906113d4565b6105c6565b005b6102c860048036038101906102c391906110f1565b6106ce565b6040516102d59190611149565b60405180910390f35b6102f860048036038101906102f391906110f1565b610743565b6040516103059190611149565b60405180910390f35b61032860048036038101906103239190611440565b610765565b6040516103359190611171565b60405180910390f35b6103586004803603810190610353919061120e565b6107e7565b005b606060038054610369906114ab565b80601f0160208091040260200160405190810160405280929190818152602001828054610395906114ab565b80156103e05780601f106103b7576101008083540402835291602001916103e0565b820191905f5260205f20905b8154815290600101906020018083116103c357829003601f168201915b5050505050905090565b5f806103f4610869565b9050610401818585610870565b600191505092915050565b5f600254905090565b5f8061041f610869565b905061042c858285610a33565b610437858585610abe565b60019150509392505050565b5f6012905090565b5f80610455610869565b90506104768185856104678589610765565b6104719190611508565b610870565b600191505092915050565b5f805f8373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f20549050919050565b6104ce610d2a565b6104d75f610da8565b565b5f60055f9054906101000a900473ffffffffffffffffffffffffffffffffffffffff16905090565b606060048054610510906114ab565b80601f016020809104026020016040519081016040528092919081815260200182805461053c906114ab565b80156105875780601f1061055e57610100808354040283529160200191610587565b820191905f5260205f20905b81548152906001019060200180831161056a57829003601f168201915b5050505050905090565b6006818051602081018201805184825260208301602085012081835280955050505050505f915054906101000a900460ff1681565b6105ce610d2a565b6006816040516105de9190611575565b90815260200160405180910390205f9054906101000a900460ff1615610639576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610630906115d5565b60405180910390fd5b6106438383610e6b565b60016006826040516106559190611575565b90815260200160405180910390205f6101000a81548160ff0219169083151502179055508273ffffffffffffffffffffffffffffffffffffffff167fdf92894dc4675a7333caa5903b69cf5d8e8ec0d3f361c88207b6688e525703bb83836040516106c19291906115f3565b60405180910390a2505050565b5f806106d8610869565b90505f6106e58286610765565b90508381101561072a576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161072190611691565b60405180910390fd5b6107378286868403610870565b60019250505092915050565b5f8061074d610869565b905061075a818585610abe565b600191505092915050565b5f60015f8473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f205f8373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f2054905092915050565b6107ef610d2a565b5f73ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff160361085d576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016108549061171f565b60405180910390fd5b61086681610da8565b50565b5f33905090565b5f73ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff16036108de576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016108d5906117ad565b60405180910390fd5b5f73ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff160361094c576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016109439061183b565b60405180910390fd5b8060015f8573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f205f8473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f20819055508173ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff167f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b92583604051610a269190611171565b60405180910390a3505050565b5f610a3e8484610765565b90507fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff8114610ab85781811015610aaa576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610aa1906118a3565b60405180910390fd5b610ab78484848403610870565b5b50505050565b5f73ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff1603610b2c576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610b2390611931565b60405180910390fd5b5f73ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1603610b9a576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610b91906119bf565b60405180910390fd5b610ba5838383610fb9565b5f805f8573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f2054905081811015610c28576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610c1f90611a4d565b60405180910390fd5b8181035f808673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f2081905550815f808573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f205f82825401925050819055508273ffffffffffffffffffffffffffffffffffffffff168473ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef84604051610d119190611171565b60405180910390a3610d24848484610fbe565b50505050565b610d32610869565b73ffffffffffffffffffffffffffffffffffffffff16610d506104d9565b73ffffffffffffffffffffffffffffffffffffffff1614610da6576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610d9d90611ab5565b60405180910390fd5b565b5f60055f9054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690508160055f6101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055508173ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff167f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e060405160405180910390a35050565b5f73ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1603610ed9576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610ed090611b1d565b60405180910390fd5b610ee45f8383610fb9565b8060025f828254610ef59190611508565b92505081905550805f808473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020015f205f82825401925050819055508173ffffffffffffffffffffffffffffffffffffffff165f73ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef83604051610fa29190611171565b60405180910390a3610fb55f8383610fbe565b5050565b505050565b505050565b5f81519050919050565b5f82825260208201905092915050565b8281835e5f83830152505050565b5f601f19601f8301169050919050565b5f61100582610fc3565b61100f8185610fcd565b935061101f818560208601610fdd565b61102881610feb565b840191505092915050565b5f6020820190508181035f83015261104b8184610ffb565b905092915050565b5f604051905090565b5f80fd5b5f80fd5b5f73ffffffffffffffffffffffffffffffffffffffff82169050919050565b5f61108d82611064565b9050919050565b61109d81611083565b81146110a7575f80fd5b50565b5f813590506110b881611094565b92915050565b5f819050919050565b6110d0816110be565b81146110da575f80fd5b50565b5f813590506110eb816110c7565b92915050565b5f80604083850312156111075761110661105c565b5b5f611114858286016110aa565b9250506020611125858286016110dd565b9150509250929050565b5f8115159050919050565b6111438161112f565b82525050565b5f60208201905061115c5f83018461113a565b92915050565b61116b816110be565b82525050565b5f6020820190506111845f830184611162565b92915050565b5f805f606084860312156111a1576111a061105c565b5b5f6111ae868287016110aa565b93505060206111bf868287016110aa565b92505060406111d0868287016110dd565b9150509250925092565b5f60ff82169050919050565b6111ef816111da565b82525050565b5f6020820190506112085f8301846111e6565b92915050565b5f602082840312156112235761122261105c565b5b5f611230848285016110aa565b91505092915050565b61124281611083565b82525050565b5f60208201905061125b5f830184611239565b92915050565b5f80fd5b5f80fd5b7f4e487b71000000000000000000000000000000000000000000000000000000005f52604160045260245ffd5b61129f82610feb565b810181811067ffffffffffffffff821117156112be576112bd611269565b5b80604052505050565b5f6112d0611053565b90506112dc8282611296565b919050565b5f67ffffffffffffffff8211156112fb576112fa611269565b5b61130482610feb565b9050602081019050919050565b828183375f83830152505050565b5f61133161132c846112e1565b6112c7565b90508281526020810184848401111561134d5761134c611265565b5b611358848285611311565b509392505050565b5f82601f83011261137457611373611261565b5b813561138484826020860161131f565b91505092915050565b5f602082840312156113a2576113a161105c565b5b5f82013567ffffffffffffffff8111156113bf576113be611060565b5b6113cb84828501611360565b91505092915050565b5f805f606084860312156113eb576113ea61105c565b5b5f6113f8868287016110aa565b9350506020611409868287016110dd565b925050604084013567ffffffffffffffff81111561142a57611429611060565b5b61143686828701611360565b9150509250925092565b5f80604083850312156114565761145561105c565b5b5f611463858286016110aa565b9250506020611474858286016110aa565b9150509250929050565b7f4e487b71000000000000000000000000000000000000000000000000000000005f52602260045260245ffd5b5f60028204905060018216806114c257607f821691505b6020821081036114d5576114d461147e565b5b50919050565b7f4e487b71000000000000000000000000000000000000000000000000000000005f52601160045260245ffd5b5f611512826110be565b915061151d836110be565b9250828201905080821115611535576115346114db565b5b92915050565b5f81905092915050565b5f61154f82610fc3565b611559818561153b565b9350611569818560208601610fdd565b80840191505092915050565b5f6115808284611545565b915081905092915050565b7f5061796d656e7420616c72656164792070726f636573736564000000000000005f82015250565b5f6115bf601983610fcd565b91506115ca8261158b565b602082019050919050565b5f6020820190508181035f8301526115ec816115b3565b9050919050565b5f6040820190506116065f830185611162565b81810360208301526116188184610ffb565b90509392505050565b7f45524332303a2064656372656173656420616c6c6f77616e63652062656c6f775f8201527f207a65726f000000000000000000000000000000000000000000000000000000602082015250565b5f61167b602583610fcd565b915061168682611621565b604082019050919050565b5f6020820190508181035f8301526116a88161166f565b9050919050565b7f4f776e61626c653a206e6577206f776e657220697320746865207a65726f20615f8201527f6464726573730000000000000000000000000000000000000000000000000000602082015250565b5f611709602683610fcd565b9150611714826116af565b604082019050919050565b5f6020820190508181035f830152611736816116fd565b9050919050565b7f45524332303a20617070726f76652066726f6d20746865207a65726f206164645f8201527f7265737300000000000000000000000000000000000000000000000000000000602082015250565b5f611797602483610fcd565b91506117a28261173d565b604082019050919050565b5f6020820190508181035f8301526117c48161178b565b9050919050565b7f45524332303a20617070726f766520746f20746865207a65726f2061646472655f8201527f7373000000000000000000000000000000000000000000000000000000000000602082015250565b5f611825602283610fcd565b9150611830826117cb565b604082019050919050565b5f6020820190508181035f83015261185281611819565b9050919050565b7f45524332303a20696e73756666696369656e7420616c6c6f77616e63650000005f82015250565b5f61188d601d83610fcd565b915061189882611859565b602082019050919050565b5f6020820190508181035f8301526118ba81611881565b9050919050565b7f45524332303a207472616e736665722066726f6d20746865207a65726f2061645f8201527f6472657373000000000000000000000000000000000000000000000000000000602082015250565b5f61191b602583610fcd565b9150611926826118c1565b604082019050919050565b5f6020820190508181035f8301526119488161190f565b9050919050565b7f45524332303a207472616e7366657220746f20746865207a65726f20616464725f8201527f6573730000000000000000000000000000000000000000000000000000000000602082015250565b5f6119a9602383610fcd565b91506119b48261194f565b604082019050919050565b5f6020820190508181035f8301526119d68161199d565b9050919050565b7f45524332303a207472616e7366657220616d6f756e74206578636565647320625f8201527f616c616e63650000000000000000000000000000000000000000000000000000602082015250565b5f611a37602683610fcd565b9150611a42826119dd565b604082019050919050565b5f6020820190508181035f830152611a6481611a2b565b9050919050565b7f4f776e61626c653a2063616c6c6572206973206e6f7420746865206f776e65725f82015250565b5f611a9f602083610fcd565b9150611aaa82611a6b565b602082019050919050565b5f6020820190508181035f830152611acc81611a93565b9050919050565b7f45524332303a206d696e7420746f20746865207a65726f2061646472657373005f82015250565b5f611b07601f83610fcd565b9150611b1282611ad3565b602082019050919050565b5f6020820190508181035f830152611b3481611afb565b905091905056fea26469706673582212200dd8889c5eb270b8a6fc40ad40cd5a35ecf0def10d299b431ff86d5a00a72efd64736f6c634300081a0033";

    private static String librariesLinkedBinary;

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_DECREASEALLOWANCE = "decreaseAllowance";

    public static final String FUNC_INCREASEALLOWANCE = "increaseAllowance";

    public static final String FUNC_MINTFROMPAYMENT = "mintFromPayment";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_PROCESSEDPAYMENTS = "processedPayments";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final Event APPROVAL_EVENT = new Event("Approval", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    public static final Event TOKENMINTED_EVENT = new Event("TokenMinted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}));
    ;

    public static final Event TRANSFER_EVENT = new Event("Transfer", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    @Deprecated
    protected PickenToken(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected PickenToken(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected PickenToken(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected PickenToken(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<ApprovalEventResponse> getApprovalEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(APPROVAL_EVENT, transactionReceipt);
        ArrayList<ApprovalEventResponse> responses = new ArrayList<ApprovalEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ApprovalEventResponse typedResponse = new ApprovalEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static ApprovalEventResponse getApprovalEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(APPROVAL_EVENT, log);
        ApprovalEventResponse typedResponse = new ApprovalEventResponse();
        typedResponse.log = log;
        typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<ApprovalEventResponse> approvalEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getApprovalEventFromLog(log));
    }

    public Flowable<ApprovalEventResponse> approvalEventFlowable(DefaultBlockParameter startBlock,
            DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(APPROVAL_EVENT));
        return approvalEventFlowable(filter);
    }

    public static List<OwnershipTransferredEventResponse> getOwnershipTransferredEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, transactionReceipt);
        ArrayList<OwnershipTransferredEventResponse> responses = new ArrayList<OwnershipTransferredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static OwnershipTransferredEventResponse getOwnershipTransferredEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, log);
        OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
        typedResponse.log = log;
        typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getOwnershipTransferredEventFromLog(log));
    }

    public Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OWNERSHIPTRANSFERRED_EVENT));
        return ownershipTransferredEventFlowable(filter);
    }

    public static List<TokenMintedEventResponse> getTokenMintedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(TOKENMINTED_EVENT, transactionReceipt);
        ArrayList<TokenMintedEventResponse> responses = new ArrayList<TokenMintedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TokenMintedEventResponse typedResponse = new TokenMintedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.to = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.orderId = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static TokenMintedEventResponse getTokenMintedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(TOKENMINTED_EVENT, log);
        TokenMintedEventResponse typedResponse = new TokenMintedEventResponse();
        typedResponse.log = log;
        typedResponse.to = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.orderId = (String) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<TokenMintedEventResponse> tokenMintedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getTokenMintedEventFromLog(log));
    }

    public Flowable<TokenMintedEventResponse> tokenMintedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TOKENMINTED_EVENT));
        return tokenMintedEventFlowable(filter);
    }

    public static List<TransferEventResponse> getTransferEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static TransferEventResponse getTransferEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(TRANSFER_EVENT, log);
        TransferEventResponse typedResponse = new TransferEventResponse();
        typedResponse.log = log;
        typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<TransferEventResponse> transferEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getTransferEventFromLog(log));
    }

    public Flowable<TransferEventResponse> transferEventFlowable(DefaultBlockParameter startBlock,
            DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));
        return transferEventFlowable(filter);
    }

    public RemoteFunctionCall<BigInteger> allowance(String owner, String spender) {
        final Function function = new Function(FUNC_ALLOWANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, owner), 
                new org.web3j.abi.datatypes.Address(160, spender)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> approve(String spender, BigInteger amount) {
        final Function function = new Function(
                FUNC_APPROVE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, spender), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> balanceOf(String account) {
        final Function function = new Function(FUNC_BALANCEOF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, account)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> decimals() {
        final Function function = new Function(FUNC_DECIMALS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> decreaseAllowance(String spender,
            BigInteger subtractedValue) {
        final Function function = new Function(
                FUNC_DECREASEALLOWANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, spender), 
                new org.web3j.abi.datatypes.generated.Uint256(subtractedValue)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> increaseAllowance(String spender,
            BigInteger addedValue) {
        final Function function = new Function(
                FUNC_INCREASEALLOWANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, spender), 
                new org.web3j.abi.datatypes.generated.Uint256(addedValue)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> mintFromPayment(String to, BigInteger amount,
            String orderId) {
        final Function function = new Function(
                FUNC_MINTFROMPAYMENT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, to), 
                new org.web3j.abi.datatypes.generated.Uint256(amount), 
                new org.web3j.abi.datatypes.Utf8String(orderId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> name() {
        final Function function = new Function(FUNC_NAME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> owner() {
        final Function function = new Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Boolean> processedPayments(String param0) {
        final Function function = new Function(FUNC_PROCESSEDPAYMENTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<TransactionReceipt> renounceOwnership() {
        final Function function = new Function(
                FUNC_RENOUNCEOWNERSHIP, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> symbol() {
        final Function function = new Function(FUNC_SYMBOL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> totalSupply() {
        final Function function = new Function(FUNC_TOTALSUPPLY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> transfer(String to, BigInteger amount) {
        final Function function = new Function(
                FUNC_TRANSFER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, to), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> transferFrom(String from, String to,
            BigInteger amount) {
        final Function function = new Function(
                FUNC_TRANSFERFROM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, from), 
                new org.web3j.abi.datatypes.Address(160, to), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> transferOwnership(String newOwner) {
        final Function function = new Function(
                FUNC_TRANSFEROWNERSHIP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, newOwner)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static PickenToken load(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        return new PickenToken(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static PickenToken load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new PickenToken(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static PickenToken load(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return new PickenToken(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static PickenToken load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new PickenToken(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<PickenToken> deploy(Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider, BigInteger initialSupply) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(initialSupply)));
        return deployRemoteCall(PickenToken.class, web3j, credentials, contractGasProvider, getDeploymentBinary(), encodedConstructor);
    }

    public static RemoteCall<PickenToken> deploy(Web3j web3j, TransactionManager transactionManager,
            ContractGasProvider contractGasProvider, BigInteger initialSupply) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(initialSupply)));
        return deployRemoteCall(PickenToken.class, web3j, transactionManager, contractGasProvider, getDeploymentBinary(), encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<PickenToken> deploy(Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit, BigInteger initialSupply) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(initialSupply)));
        return deployRemoteCall(PickenToken.class, web3j, credentials, gasPrice, gasLimit, getDeploymentBinary(), encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<PickenToken> deploy(Web3j web3j, TransactionManager transactionManager,
            BigInteger gasPrice, BigInteger gasLimit, BigInteger initialSupply) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(initialSupply)));
        return deployRemoteCall(PickenToken.class, web3j, transactionManager, gasPrice, gasLimit, getDeploymentBinary(), encodedConstructor);
    }

    public static void linkLibraries(List<Contract.LinkReference> references) {
        librariesLinkedBinary = linkBinaryWithReferences(BINARY, references);
    }

    private static String getDeploymentBinary() {
        if (librariesLinkedBinary != null) {
            return librariesLinkedBinary;
        } else {
            return BINARY;
        }
    }

    public static class ApprovalEventResponse extends BaseEventResponse {
        public String owner;

        public String spender;

        public BigInteger value;
    }

    public static class OwnershipTransferredEventResponse extends BaseEventResponse {
        public String previousOwner;

        public String newOwner;
    }

    public static class TokenMintedEventResponse extends BaseEventResponse {
        public String to;

        public BigInteger amount;

        public String orderId;
    }

    public static class TransferEventResponse extends BaseEventResponse {
        public String from;

        public String to;

        public BigInteger value;
    }
}
