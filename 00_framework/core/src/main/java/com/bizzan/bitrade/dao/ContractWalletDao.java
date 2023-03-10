package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.Coin;
import com.bizzan.bitrade.entity.ContractWallet;
import com.bizzan.bitrade.entity.MemberWallet;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

public interface ContractWalletDao extends BaseDao<ContractWallet> {

    /**
     * 增加钱包余额
     *
     * @param walletId
     * @param amount
     * @return
     */
    @Modifying
    @Query("update ContractWallet wallet set wallet.balance = wallet.balance + :amount where wallet.id = :walletId")
    int increaseBalance(@Param("walletId") long walletId, @Param("amount") BigDecimal amount);

    /**
     * 减少钱包余额
     *
     * @param walletId
     * @param amount
     * @return
     */
    @Modifying
    @Query("update ContractWallet wallet set wallet.balance = wallet.balance - :amount where wallet.id = :walletId and wallet.balance >= :amount")
    int decreaseBalance(@Param("walletId") long walletId, @Param("amount") BigDecimal amount);

    /**
     * 冻结钱包余额
     *
     * @param walletId
     * @param amount
     * @return
     */
    @Modifying
    @Query("update ContractWallet wallet set wallet.balance = wallet.balance - :amount,wallet.frozenBalance=wallet.frozenBalance + :amount where wallet.id = :walletId and wallet.balance >= :amount")
    int freezeBalance(@Param("walletId") long walletId, @Param("amount") BigDecimal amount);

    /**
     * 解冻钱包余额
     *
     * @param walletId
     * @param amount
     * @return
     */
    @Modifying
    @Query("update ContractWallet wallet set wallet.balance = wallet.balance + :amount,wallet.frozenBalance=wallet.frozenBalance - :amount where wallet.id = :walletId and wallet.frozenBalance >= :amount")
    int thawBalance(@Param("walletId") long walletId, @Param("amount") BigDecimal amount);

    /**
     * 减少冻结余额
     *
     * @param walletId
     * @param amount
     * @return
     */
    @Modifying
    @Query("update ContractWallet wallet set wallet.frozenBalance=wallet.frozenBalance - :amount where wallet.id = :walletId and wallet.frozenBalance >= :amount")
    int decreaseFrozen(@Param("walletId") long walletId, @Param("amount") BigDecimal amount);

    /**
     * 增加冻结资产
     *
     * @param id
     * @param amount
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query("update ContractWallet wallet set wallet.frozenBalance=wallet.frozenBalance + :amount where wallet.id = :walletId")
    int increaseFrozen(@Param("walletId") Long walletId, @Param("amount") BigDecimal amount);

    ContractWallet findByCoinAndMemberId(Coin coin, Long memberId);

    List<ContractWallet> findAllByMemberId(Long memberId);

    @Query(value = "select sum(a.balance)+sum(a.frozen_balance) as allBalance from contract_wallet a where a.coin_id = :coinName", nativeQuery = true)
    BigDecimal getWalletAllBalance(@Param("coinName") String coinName);

}
