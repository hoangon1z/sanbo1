package com.bizzan.bitrade.service;

import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.constant.TransferDirection;
import com.bizzan.bitrade.dao.CoinDao;
import com.bizzan.bitrade.dao.ContractWalletDao;
import com.bizzan.bitrade.dao.MemberDao;
import com.bizzan.bitrade.dao.MemberWalletDao;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.exception.InformationExpiredException;
import com.bizzan.bitrade.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class ContractWalletService {
    @Autowired
    private MemberDao memberDao;
    @Autowired
    private CoinDao coinDao;
    @Autowired
    private ContractWalletDao contractWalletDao;
    @Autowired
    private MemberWalletDao memberWalletDao;
    @Autowired
    private TransferService transferService;
    @Autowired
    private LocaleMessageSourceService msService;

    /**
     * 保存钱包
     *
     * @param wallet
     * @return
     */
    public ContractWallet save(ContractWallet wallet) {
        return contractWalletDao.saveAndFlush(wallet);
    }

    /**
     * 根据币种和用户ID获取钱包
     *
     * @param coin
     * @param member
     * @return
     */
    public ContractWallet findByCoinAndMember(Coin coin, Member member) {
        return contractWalletDao.findByCoinAndMemberId(coin, member.getId());
    }

    public ContractWallet findByCoinUnitAndMemberId(String coinUnit, Long memberId) {
        Coin coin = coinDao.findByUnit(coinUnit);
        return contractWalletDao.findByCoinAndMemberId(coin, memberId);
    }

    public ContractWallet findByCoinAndMemberId(Coin coin, Long memberId) {
        return contractWalletDao.findByCoinAndMemberId(coin, memberId);
    }

    /**
     * 根据用户查找所有钱包
     *
     * @param member
     * @return
     */
    public List<ContractWallet> findAllByMemberId(Member member) {
        return contractWalletDao.findAllByMemberId(member.getId());
    }

    public List<ContractWallet> findAllByMemberId(Long memberId) {
        return contractWalletDao.findAllByMemberId(memberId);
    }

    /**
     * 冻结钱包（与余额有关）
     *
     * @param memberWallet
     * @param amount
     * @return
     */
    public MessageResult freezeBalance(ContractWallet memberWallet, BigDecimal amount) {
        int ret = contractWalletDao.freezeBalance(memberWallet.getId(), amount);
        if (ret > 0) {
            return MessageResult.success();
        } else {
            return MessageResult.error("Information Expired");
        }
    }

    /**
     * 解冻钱包（与余额有关）
     *
     * @param memberWallet
     * @param amount
     * @return
     */
    public MessageResult thawBalance(ContractWallet memberWallet, BigDecimal amount) {
        int ret = contractWalletDao.thawBalance(memberWallet.getId(), amount);
        if (ret > 0) {
            return MessageResult.success();
        } else {
            return MessageResult.error("Information Expired");
        }
    }

    /**
     * 锁定钱包
     *
     * @param uid
     * @param unit
     * @return
     */
    @Transactional
    public boolean lockWallet(Long uid, String unit) {
        ContractWallet wallet = findByCoinUnitAndMemberId(unit, uid);
        if (wallet != null && wallet.getIsLock() == BooleanEnum.IS_FALSE) {
            wallet.setIsLock(BooleanEnum.IS_TRUE);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 解锁钱包
     *
     * @param uid
     * @param unit
     * @return
     */
    @Transactional
    public boolean unlockWallet(Long uid, String unit) {
        ContractWallet wallet = findByCoinUnitAndMemberId(unit, uid);
        if (wallet != null && wallet.getIsLock() == BooleanEnum.IS_TRUE) {
            wallet.setIsLock(BooleanEnum.IS_FALSE);
            return true;
        } else {
            return false;
        }
    }

    public BigDecimal getAllBalance(String coinName) {
        return contractWalletDao.getWalletAllBalance(coinName);
    }

    /**
     * 增加余额
     *
     * @param walletId
     * @param amount
     */
    public void increaseBalance(Long walletId, BigDecimal amount) {
        contractWalletDao.increaseBalance(walletId, amount);
    }

    /**
     * 增加冻结资产（与余额无关）
     *
     * @param id
     * @param amount
     */
    public void increaseFrozen(Long id, BigDecimal amount) {
        contractWalletDao.increaseFrozen(id, amount);
    }

    /**
     * 减少冻结资产（与余额无关）
     *
     * @param walletId
     * @param amount
     */
    public void decreaseFrozen(Long walletId, BigDecimal amount) {
        contractWalletDao.decreaseFrozen(walletId, amount);
    }

    /**
     * 币币账户转入合约账户
     *
     * @param memberId
     * @param coinUnit
     * @param amount
     * @return
     */
    @Transactional
    public MessageResult transferIn(long memberId, String coinUnit, BigDecimal amount) throws InformationExpiredException {
        Coin coin = coinDao.findByUnit(coinUnit);
        ContractWallet contractWallet = findByCoinAndMemberId(coin, memberId);
        MemberWallet memberWallet = memberWalletDao.findByCoinAndMemberId(coin, memberId);
        if (memberWallet.getBalance().compareTo(amount) < 0) {
            return MessageResult.error(500, msService.getMessage("AMOUNT_OVER_SIZE"));
        }
        Transfer transfer = new Transfer();
        transfer.setAmount(amount);
        transfer.setCoin(coin);
        transfer.setMemberId(memberId);
        transfer.setFee(BigDecimal.ZERO);
        transfer.setDirection(TransferDirection.contract);
        transferService.save(transfer);
        //todo 乐观锁？
        int i = memberWalletDao.decreaseBalance(memberWallet.getId(), amount);
        if (i <= 0) {
            throw new InformationExpiredException("Information Expired");
        }
        int i1 = contractWalletDao.increaseBalance(contractWallet.getId(), amount);
        if (i1 <= 0) {
            throw new InformationExpiredException("Information Expired");
        }
        return MessageResult.success();
    }

    /**
     * 合约账户转入币币账户
     *
     * @param memberId
     * @param coinUnit
     * @param amount
     * @return
     */
    @Transactional
    public MessageResult transferOut(long memberId, String coinUnit, BigDecimal amount) throws InformationExpiredException {
        Coin coin = coinDao.findByUnit(coinUnit);
        ContractWallet contractWallet = findByCoinAndMemberId(coin, memberId);
        MemberWallet memberWallet = memberWalletDao.findByCoinAndMemberId(coin, memberId);
        if (contractWallet.getBalance().compareTo(amount) < 0) {
            return MessageResult.error(500, msService.getMessage("AMOUNT_OVER_SIZE"));
        }
        Transfer transfer = new Transfer();
        transfer.setAmount(amount);
        transfer.setCoin(coin);
        transfer.setMemberId(memberId);
        transfer.setFee(BigDecimal.ZERO);
        transfer.setDirection(TransferDirection.exchange);
        transferService.save(transfer);
        //todo 乐观锁？
        int i = contractWalletDao.decreaseBalance(contractWallet.getId(), amount);
        if (i <= 0) {
            throw new InformationExpiredException("Information Expired");
        }
        int i1 = memberWalletDao.increaseBalance(memberWallet.getId(), amount);
        if (i1 <= 0) {
            throw new InformationExpiredException("Information Expired");
        }
        return MessageResult.success();
    }
}
