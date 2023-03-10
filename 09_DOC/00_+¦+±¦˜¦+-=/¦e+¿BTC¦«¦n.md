# Ubuntu 16.04 搭建BTC钱包

## 磁盘挂载（如有必要）

https://help.aliyun.com/document_detail/25426.html?spm=a2c4g.11186623.6.610.yemgNN

## 安装

```
cd /mnt
wget https://bitcoin.org/bin/bitcoin-core-0.16.1/bitcoin-0.16.1-x86_64-linux-gnu.tar.gz
tar -xzvf bitcoin-0.16.1-x86_64-linux-gnu.tar.gz
```

[其他版本下载](https://bitcoin.org/en/download)

## 配置

```
mkdir bitcoin
cd bitcoin
vim bitcoin.conf
```

```
listen=1
server=1
rpcuser=bitcoinuser
rpcpassword=kmgcB6MKdpXDvcjXeR8nubP9TrRnmtzN
rpcport=7010
rpcallowip=127.0.0.1
```

## 启动

```
nohup /mnt/bitcoin-0.16.1/bin/./bitcoind --datadir=/mnt/bitcoin &
```

## 创建启动脚本

```
vim start_service.sh
```

```
#!/bin/bash
nohup /mnt/bitcoin-0.16.1/bin/./bitcoind --datadir=/mnt/bitcoin --conf=/mnt/bitcoin/bitcoin.conf &
exit 0
```

```
sudo chmod +x start_service.sh
sudo mv start_service.sh /etc/init.d/
cd /etc/init.d/
sudo update-rc.d start_service.sh defaults 90
```

## 关闭
bitcoin-cli stop
## 查看区块链同步
bitcoin-cli getblockchaininfo
bitcoin-cli getmininginfo
## 区块浏览器
1.https://btc.com/
2.http://blockchain.info

## rpc API 访问
#curl访问rpc测试
curl -s -X POST --user bitcoinuser:kmgcB6MKdpXDvcjXeR8nubP9TrRnmtzN  \
  -H 'content-type: text/plain;' http://127.0.0.1:7010/ \
  --data-binary '{"jsonrpc": "1.0", "id":"curltest", "method": "getmininginfo", "params": [] }' 
#查询最新区块高度
curl -s -X POST --user bitcoinuser:kmgcB6MKdpXDvcjXeR8nubP9TrRnmtzN  \
  -H 'content-type: text/plain;' http://127.0.0.1:7010/ \
  --data-binary '{"jsonrpc": "1.0", "id":"curltest", "method": "getmininginfo", "params": [] }' \
  |awk -F '[:,]' '{print $3}'
配置参数说明
rpcuser     远程访问的认证用户
rpcpassword 远程访问密码
daemon      在后台运行
rpcallowip  远程访问的ip或网段
txindex     所有交易进行索引；否则只保留钱包地址交易索引记录  
deprecatedrpc=accounts 启用账户"account"API ,V0.18版将被完全移除,新版"标签-label"API

