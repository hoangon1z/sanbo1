<template>
 <div>
	 <!-- 待优化 2018年4月16日15:33:35 -->
    <Card>
			<p slot="title">
			  合约
        <Button type="primary" size="small" @click="refreshPageManual">
          <Icon type="refresh"></Icon>刷新
        </Button>
			</p>
      <Row class="functionWrapper" style='padding:0px 8px 8px 8px'>
        <Col span="18">
          <div class="searchWrapper" style="float:left;">
              <div class="poptip">
                  <Input placeholder="币种名称" v-model="searchSymbol" /></Input>
              </div>
              <div class="poptip">/</div>
              <div class="poptip">
                  <Input placeholder="基础币种名" v-model="searchBase" /></Input>
              </div>
              <div class="btns">
                  <Button type="info" @click="search">搜索</Button>
              </div>
          </div>
        </Col>
        <Col span="6">
          <div style="float: right" class="clearfix">
            <Button type="error" @click="delcoin" style="display: none;">删除合约</Button>
            <Button type="primary"@click="addcoin">添加合约</Button>

          </div>
        </Col>
      </Row>

      <Modal
					class="auditModel"
					v-model="loginPassModal"
					title="请输入登录密码"
					width="350"
					@on-cancle="loginPW = ''"
					@on-ok="confirmLoginPass">
					<Input v-model="loginPW" type="password" placeholder="请输入登录密码"></Input>
			 </Modal>

      <Modal
          class="auditModel"
          v-model="fixModel"
          title="修改信息"
          @on-ok="confirmClicked">
          <ul>
            <li><span><i>*</i> 合约名称：</span>
              <p>
                <Input v-model="fixName"></Input>
              </p>
            </li>
            <li><span><i>*</i> 合约类型：</span>
              <p>
                <RadioGroup v-model="fixType">
                  <Radio label="ALWAYS"><em>永续合约</em></Radio>
                  <Radio label="SECOND"><em>秒合约</em></Radio>
                </RadioGroup>
              </p>
            </li>
            <li><span><i>*</i> 交易对：</span>
              <p>
                <Input v-model="fixSymbol" disabled></Input>
                <span>{{ }}</span>
              </p>
            </li>

            <li><span><i>*</i> 手续费：</span>
                <p><Input v-model="fixOpenFee"
                 :class="{'errorFormatBorder': feeClass}"
                  @on-change="checkFee(fixOpenFee)"></Input>
                </p>
                <em v-show="feeClass">格式不正确</em>
            </li>

            <li><span>买入手续费：</span>
              <p><Input v-model="fixTakerFee"
                :class="{'errorFormatBorder': feeClass}"
                @on-change="checkTakerFee(fixTakerFee)" placeholder="例: 0.001"></Input>
              </p>
              <em v-show="takerFeeClass">格式不正确</em>
            </li>
            <li><span>卖出手续费：</span>
              <p><Input v-model="fixMakerFee"
                :class="{'errorFormatBorder': feeClass}"
                @on-change="checkMakerFee(fixMakerFee)" placeholder="例: 0.001"></Input>
              </p>
              <em v-show="makerFeeClass">格式不正确</em>
            </li>

            <li><span><i>*</i> 维持保证金率：</span>
                <p><Input v-model="fixMaintenanceMarginRate"
                 :class="{'errorFormatBorder': maintenanceMarginRateClass}"
                  @on-change="checkMaintenanceMarginRate(fixMaintenanceMarginRate)"></Input>
                </p>
                <em v-show="maintenanceMarginRateClass">格式不正确</em>
            </li>

            <li><span><i>*</i> 状态：</span>
              <p>
                <RadioGroup v-model="fixEnable">
                  <Radio label="1"><em>启用(上架)</em></Radio>
                  <Radio label="2"><em>禁止(下架)</em></Radio>
                </RadioGroup>
              </p>
            </li>
            <li><span><i>*</i> 前端显示：</span>
              <p>
                <RadioGroup v-model="fixVisible">
                  <Radio label="1"><em>显示</em></Radio>
                  <Radio label="2"><em>隐藏</em></Radio>
                </RadioGroup>
              </p>
            </li>
            <li><span><i>*</i> 是否可交易：</span>
              <p>
                <RadioGroup v-model="fixExchangeable">
                  <Radio label="1"><em>是</em></Radio>
                  <Radio label="2"><em>否</em></Radio>
                </RadioGroup>
              </p>
            </li>
            <li><span><i>*</i> 市价买：</span>
              <p>
                <RadioGroup v-model="fixEnableMarketBuy">
                  <Radio label="1"><em>可以</em></Radio>
                  <Radio label="0"><em>不可以</em></Radio>
                </RadioGroup>
              </p>
            </li>
            <li><span><i>*</i> 市价卖：</span>
              <p>
                <RadioGroup v-model="fixEnableMarketSell">
                  <Radio label="1"><em>可以</em></Radio>
                  <Radio label="0"><em>不可以</em></Radio>
                </RadioGroup>
              </p>
            </li>
            <li><span><i>*</i> 排序：</span>
              <p><Input v-model="fixSort"
                        :class="{'errorFormatBorder': sortClass}"
                        @on-change="checksort(fixSort)" placeholder="例：5，须大于4(必须)"></Input>
              </p>
              <em v-show="sortClass">格式不正确</em>
            </li>

            <li><span><i>*</i> 最小下单量：</span>
                <p><Input v-model="fixMinShare"></Input>
                </p>
            </li>
            <li><span><i>*</i> 最大下单量：</span>
                <p><Input v-model="fixMaxShare"></Input>
                </p>
            </li>
          </ul>

      </Modal>
      <Modal
          class="auditModel"
          v-model="auditModal"
          title="添加合约"
          @on-ok="confirmAudit">
          <Row :gutter="30">
            <Col span="22">
              <p class="setting-title">基本信息设置</p>
              <ul>
                <li><span><i>*</i> 合约名称：</span>
                  <p>
                    <Input v-model="name" placeholder="例: BTC永续(必须)"></Input>
                  </p>
                </li>
                <li><span><i>*</i> 合约类型：</span>
                  <p>
                    <RadioGroup v-model="type">
                      <Radio label="ALWAYS"><em>永续合约</em></Radio>
                      <Radio label="SECOND"><em>秒合约</em></Radio>
                    </RadioGroup>
                  </p>
                </li>
                <li><span><i>*</i> 交易对：</span>
                  <p>
                    <Input v-model="symbol"
                           :class="{'errorFormatBorder': checkSymbolClass}"
                           @on-change="checkPair(symbol)" placeholder="例: BTC/USDT(必须)"></Input>
                  </p>
                  <em v-show="checkSymbolClass">格式不正确</em>
                </li>
                <li><span><i>*</i> 交易币种：</span>
                  <p><Input v-model="coinSymbol"
                            :class="{'errorFormatBorder': coinSymbolClass}"
                            @on-change="checkCoinSymbol(coinSymbol)"  placeholder="例: BTC(必须)"></Input>
                  </p>
                  <em v-show="coinSymbolClass">格式不正确</em>
                </li>
                <li><span><i>*</i> 基础币种：</span>
                  <p><Input v-model="baseSymbol"
                            :class="{'errorFormatBorder': baseSymbolClass}"
                            @on-change="checkbaseSymbol(baseSymbol)" placeholder="例: USDT(必须)"></Input>
                  </p>
                   <em v-show="baseSymbolClass">格式不正确</em>
                </li>
                <li><span><i>*</i> 合约面值：</span>
                  <p><Input v-model="shareNumber"
                   :class="{'errorFormatBorder': feeClass}"
                    @on-change="checkShareNumber(shareNumber)" placeholder="例: 10(必须)"></Input>
                  </p>
                  <em v-show="shareNumberClass">格式不正确</em>
                </li>
                <li><span><i>*</i> 维持保证金率：</span>
                  <p><Input v-model="maintenanceMarginRate"
                   :class="{'errorFormatBorder': maintenanceMarginRateClass}"
                    @on-change="checkMaintenanceMarginRate(maintenanceMarginRate)" placeholder="例: 0.001(必须)"></Input>
                  </p>
                  <em v-show="maintenanceMarginRateClass">格式不正确</em>
                </li>
                <li><span><i>*</i> 开仓手续费：</span>
                  <p><Input v-model="openFee"
                   :class="{'errorFormatBorder': feeClass}"
                    @on-change="checkFee(openFee)" placeholder="例: 0.001(必须)"></Input>
                  </p>
                  <em v-show="feeClass">格式不正确</em>
                </li>
                <li><span>买入手续费：</span>
                  <p><Input v-model="takerFee"
                   :class="{'errorFormatBorder': feeClass}"
                    @on-change="checkTakerFee(takerFee)" placeholder="例: 0.001"></Input>
                  </p>
                  <em v-show="takerFeeClass">格式不正确</em>
                </li>
                <li><span>卖出手续费：</span>
                  <p><Input v-model="makerFee"
                   :class="{'errorFormatBorder': feeClass}"
                    @on-change="checkMakerFee(openFee)" placeholder="例: 0.001"></Input>
                  </p>
                  <em v-show="makerFeeClass">格式不正确</em>
                </li>
                <li><span><i>*</i> 币种精度：</span>
                  <p><Input v-model="coinScale"
                            :class="{'errorFormatBorder': coinScaleClass}"
                            @on-change="checkCoinScale(coinScale)" placeholder="例: 4(必须)"></Input>
                  </p>
                  <em v-show="coinScaleClass">格式不正确</em>
                </li>
                <li><span><i>*</i> 最小下单量：</span>
                  <p><Input v-model="minShare"
                            :class="{'errorFormatBorder': minVolumeClass}"
                            @on-change="checkminVolume(minShare)" placeholder="不限制: 1(必须)"></Input>
                  </p>
                  <em v-show="minVolumeClass">格式不正确</em>
                </li>
                <li><span><i>*</i> 最大下单量：</span>
                  <p><Input v-model="maxShare"
                            :class="{'errorFormatBorder': maxVolumeClass}"
                            @on-change="checkmaxVolume(maxShare)" placeholder="不限制: 100(必须)"></Input>
                  </p>
                  <em v-show="maxVolumeClass">格式不正确</em>
                </li>
                <!-- <li><span><i>*</i> 交易区：</span>
                  <p><Input v-model="zone"
                            :class="{'errorFormatBorder': zoneClass}"
                            @on-change="checkzone(zone)" placeholder="0:主板, 1:创新板(必须)"></Input>
                  </p>
                  <em v-show="zoneClass">格式不正确</em>
                </li> -->
                <li><span><i>*</i>排序：</span>
                  <p><Input v-model="sort"
                            :class="{'errorFormatBorder': sortClass}"
                            @on-change="checksort(sort)" placeholder="例：5，须大于4(必须)"></Input>
                  </p>
                  <em v-show="sortClass">格式不正确</em>
                </li>
              </ul>
            </Col>      
          </Row>
      </Modal>

      <Table
        border
        :columns="columns_first"
        :data="exchange"
        @on-selection-change="selected"
        :loading="ifLoading">
      </Table>

      <Row class="pageWrapper">
        <Page
        :total="pageNum"
        class="buttomPage"
        :current="current"
        @on-change="changePage"
        show-elevator></Page>
      </Row>
      <p style="font-size:11px;">注意1：【状态】= 下架，无论【可交易】的状态为是或否，都无法交易，并且前台也不显示</p>
      <p style="font-size:11px;">注意2：【状态】= 上架，且【可交易】= 是，可以正常交易</p>
      <p style="font-size:11px;">注意3：【状态】= 上架，且【可交易】= 否，不可以交易</p>
      <p style="font-size:11px;">注意4：【状态】= 上架，且【显示】= 是，前台显示</p>
      <p style="font-size:11px;">注意5：维持保证金率，(保证金-未实现盈亏)/保证金<=维持保证金率 触发强制平仓</p>
      <p style="font-size:11px;">注意6：若未分别设置买入手续费、卖出手续费，系统自动使用开仓手续费计算</p>
      <p style="font-size:11px;">注意7：手续费=(合约面值*合约张数)*手续费率</p>
      <Modal
        v-model="ifDelete"
        title="删除币种"
        @on-ok="confirmDel"
        @on-cancel="$Message.info('已取消！')">
        <p>是否删除选中的{{ deleteArr.length }}项</p>
       </Modal>
    </Card>
 </div>
</template>

 <script>
import dtime from 'time-formater'
import { transactionPair } from '@/caculate/caculate'
import { queryContractCoin, addContractCoin, editContractCoin } from '@/service/getData'

export default {
  data() {
    return {
			loginPW: '',
			loginPassModal: false,
      ifLoading: true,
      // currentPageIdx: 1,
      symbolClass: false,
      coinScaleClass: false,
      baseSymbolClass: false,
      checkSymbolClass: false,
      coinSymbolClass: false,
      feeClass: false,
      baseCoinScaleClass: false,
      minVolumeClass: false,
      maxVolumeClass: false,
      zoneClass: false,
      minSellPriceClass: false,
      maxBuyPriceClass: false,
      minTurnoverClass: false,
      takerFeeClass: false,
      makerFeeClass: false,
      sortClass: false,
      publishPriceClass: false,
      shareNumberClass: false,
      maintenanceMarginRateClass: false,
      operation: 1, // 1: 设置   2：启动引擎   3：停止引擎   4：撤销委托

      fixId: null,
      fixName: null,
      fixType: null,
      fixSymbol: null,
      fixEnable: 1,
      fixModel: false,
      fixSort: null,
      fixVisible: 1,
      fixExchangeable: 1,
      fixEnableMarketSell: "1",
      fixEnableMarketBuy: "1",
      fixMinShare: null,
      fixMaxShare: null,
      fixMaintenanceMarginRate: null,
      fixOpenFee: null,
      fixTakerFee: null,
      fixMakerFee: null,

      startEngineModel: false,
      stopEngineModel: false,
      cancelAllModel: false,
      targetSymbol: null,

      searchSymbol: "",
      searchBase: "",
      name: null,
      type: "ALWAYS",
      symbol: null,
      coinSymbol: null,
      baseSymbol: null,
      enable: "1",
      openFee: null,
      coinScale: null,
      shareNumber: null,
      takerFee: null, // add
      makerFee: null, // add
      minShare: null, // add
      maxShare: null, // add
      maintenanceMarginRate: null, // add
      sort: null, // add
      publishPrice: null, // add  分摊抢购初始价格
      publishType: "1", // 发行活动：1-无活动  2-抢购   3-分摊
      publishAmount: 0,
      startTime: null,
      endTime: null,
      clearTime: null,
      visible: "1",
      exchangeable: "1",
      enableMarketBuy: "1",
      enableMarketSell: "1",

      auditModal: false,
      pageNum: null,
      current: 1,
      deleteArr: false,
      ifDelete: false,

      exchangeEngineModel: false,
      engineTargetSymbol: "",
      engineLimitSellCount: "",
      engineLimitBuyCount: "",
      engineMarketSellCount: "",
      engineMarketBuyCount: "",
      engineBuyDepth: 0,
      engineSellDepth: 0,
      columns_first: [
        {
          type: "selection",
          align: 'center'
        },
        {
          title: "合约名称",
          key: "name"
        },
        {
          title: "交易对",
          key: "symbol",
          render: (h, params) => {
            const row = params.row;
            return h("div", {
                style:{
                  textAlign: "center"
                }
              }, [
                h("span", {style:{fontWeight:"bold"}}, row.symbol)
              ]);
          }
        },
        {
          title: "合约面值",
          key: "shareNumber"
        },
        {
          title: "显示",
          key: "visible",
          render: (h, params) => {
            const row = params.row;
            const visible = row.visible == "1" ? "是" : "否";
            if(row.visible == "1") {
              return h("span", {
                style:{
                  color:'#42b983'
                }
              }, visible);
            }
            return h("span", {
              style:{
                  color:'#FF0000'
                }
            }, visible);
          }
        },
        {
          title: "可交易",
          key: "exchangeable",
          render: (h, params) => {
            const row = params.row;
            const exchangeable = row.exchangeable == "1" ? "是" : "否";
            if(row.exchangeable == "1") {
              return h("span", {
                style:{
                  color:'#42b983'
                }
              }, exchangeable);
            }
            return h("span", {
              style:{
                  color:'#FF0000'
                }
            }, exchangeable);
          }
        },
        {
          title: "状态",
          key: "enable",
          render: (h, params) => {
            const row = params.row;
            const enable = row.enable == "1" ? "上架" : "下架";
            if(row.enable == "1") {
              return h("span", {
                style:{
                  color:'#42b983'
                }
              }, enable);
            }
            return h("span", {
              style:{
                  color:'#FF0000'
                }
            }, enable);
          }
        },
        {
          title: '排序',
          key:"sort"
        },
        {
          title: "开仓手续费",
          key: "openFee",
          render: (h, params) => {
            const row = params.row;
            return h("div", {
                style:{
                  textAlign: "center"
                }
              }, [
                h("span", {}, ((row.takerFee||row.openFee) * 1000 + "‰") + '-' + ((row.makerFee||row.openFee) * 1000 + "‰"))
              ]);
          }
        },
        {
          title: '币种精度',
          key:"coinScale"
        },
        {
          title: '最小下单量',
          key:"minShare"
        },
        {
          title: '最大下单量',
          key:"maxShare"
        },
        {
          title: '维持保证金率',
          key: 'maintenanceMarginRate',
          render: (h, params) => {
            const row = params.row;
            return h("div", {
                style:{
                  textAlign: "center"
                }
              }, [
                h("span", {}, row.maintenanceMarginRate * 100 + "%")
              ]);
          }
        },
        {
          title: "操作",
          key: "xx",
          width: 250,
          fixed: 'right',
          render: (h, obj) => {
            return h("div", [
              h(
                "Button",
                {
                  props: {type: "info",size: "small",icon:"android-settings"},
                  style: {marginRight: "5px"},
                  on: {
                    click: () => {
                      this.operation = 1;
                      this.fixModel = true;
                      this.fixId = obj.row.id;
                      this.fixName = obj.row.name;
                      this.fixType = obj.row.type;
                      this.fixSymbol = obj.row.symbol;
                      this.fixOpenFee = obj.row.openFee;
                      this.fixTakerFee = obj.row.takerFee;
                      this.fixMakerFee = obj.row.makerFee;
                      this.fixSort = obj.row.sort;
                      this.fixEnable = String(obj.row.enable);
                      this.fixVisible = String(obj.row.visible);
                      this.fixExchangeable = String(obj.row.exchangeable);
                      this.fixEnableMarketBuy = String(obj.row.enableMarketBuy);
                      this.fixEnableMarketSell = String(obj.row.enableMarketSell);
                      this.fixMaintenanceMarginRate = obj.row.maintenanceMarginRate;
                      this.fixMinShare = obj.row.minShare;
                      this.fixMaxShare = obj.row.maxShare;
                    }
                  }
                },
                "设置"
              )
            ]);
          }
        }
      ],
      exchange: []
    };
  },
  methods: {
    checkbaseCoinScale(str) {
      let bol = !(str*1>=0&&String(str).split('.').length===1)
                || str===null || !str.trim().length;
      this.baseCoinScaleClass =  bol;
    },
    checkCoinScale(str) {
      let bol = !(str*1>=0&&String(str).split('.').length===1)
                || str===null || !str.trim().length;
      this.coinScaleClass =  bol;
    },
    checkFee(str) {
      let re = /\d|\d+.\d+$/g;
      this.feeClass = !re.test(String(str)) || !(str*1>0);
    },
    checkTakerFee(str) {
      let re = /\d|\d+.\d+$/g;
      this.takerFeeClass = !re.test(String(str)) || !(str*1>0);
    },
    checkMakerFee(str) {
      let re = /\d|\d+.\d+$/g;
      this.makerFeeClass = !re.test(String(str)) || !(str*1>0);
    },
    checkMaintenanceMarginRate(str) {
      let re = /\d|\d+.\d+$/g;
      this.maintenanceMarginRateClass = !re.test(String(str)) || !(str*1>0);
    },
    checkbaseSymbol(str) {
      let re = /[^A-Z]/g;
      this.baseSymbolClass =  re.test(str);
    },
    checkCoinSymbol(str) {
      let re = /[^A-Z]/g;
      this.coinSymbolClass =  re.test(str);
    },
    checkPair(str) {
     let re = /^[A-Z]+\/[A-Z]+$/g;
      this.checkSymbolClass = !re.test(str);
      console.log(this.checkSymbolClass);
    },
    checkShareNumber(str) {
      let re = /\d$/g;
      this.shareNumberClass = !re.test(str);
    },
    checkminVolume(str) {
      let re = /\d$/g;
      this.minVolumeClass = !re.test(String(str));
    },
    checkmaxVolume(str) {
      let re = /\d$/g;
      this.maxVolumeClass = !re.test(String(str));
    },
    checkzone(str) {
      let bol = !(str*1>=0&&String(str).split('.').length===1)
                || str===null || !str.trim().length;
      this.zoneClass =  bol;
    },
    checkminSellPrice(str) {
      let re = /\d|\d+.\d+$/g;
      this.minSellPriceClass = !re.test(String(str));
    },
    checkmaxBuyPrice(str) {
      let re = /\d|\d+.\d+$/g;
      this.maxBuyPriceClass = !re.test(String(str));
    },
    checkminTurnover(str) {
      let re = /\d|\d+.\d+$/g;
      this.minTurnoverClass = !re.test(String(str));
    },
    checksort(str) {
      let bol = !(str*1>=0&&String(str).split('.').length===1)
                || str===null || !str.trim().length;
      this.sortClass =  bol;
    },
    checkpublishPrice(str) {
      let re = /\d|\d+.\d+$/g;
      this.publishPriceClass = !re.test(String(str));
    },
		confirmLoginPass() {
      this.$Loading.start();
      if(this.operation == 1){
          // 修改币币信息
  			  this.feeClass = false;
          let obj ={
            id: this.fixId,
            name: this.fixName,
            symbol:  this.fixSymbol,
            openFee: this.fixOpenFee,
            takerFee: this.fixTakerFee,
            makerFee: this.fixMakerFee,
  					enable: this.fixEnable,
            visible: this.fixVisible,
            exchangeable: this.fixExchangeable,
            enableMarketSell: this.fixEnableMarketSell,
            enableMarketBuy: this.fixEnableMarketBuy,
            sort: this.fixSort,
            minShare: this.fixMinShare,
            maxShare: this.fixMaxShare,
            maintenanceMarginRate: this.fixMaintenanceMarginRate,
  					password: this.loginPW
  				}

          editContractCoin(obj).then( res => {
            if (!res.code) {
  						this.$Message.success('修改成功！');
              this.current = 1;
              this.refreshdata(1);
              this.$Loading.finish();
            }else {
              this.$Message.error('修改失败！');
              this.$Loading.error();
            }
          });
      }
		},
    confirmClicked() {
      this.loginPassModal =  true;
      
    },
    confirmAudit() {
      let judgeCondition = this.symbolClass || this.coinSymbolClass || this.baseSymbolClass || this.shareNumberClass || this.maintenanceMarginRateClass ||
                  this.openFeeClass ||this.takerFeeClass ||this.makerFeeClass || this.coinScaleClass || this.minShareClass ||
                  this.maxShareClass || this.sortClass;
      let judgeEmpty = !this.name || !this.type || !this.symbol || !this.coinSymbol || !this.baseSymbol || !this.shareNumber || !this.shareNumber || !this.maintenanceMarginRate || !this.openFee || !this.coinScale || !this.minShare
                      || !this.maxShare

      if(judgeCondition || judgeEmpty) {
        this.$Message.warning('信息录入不正确！');
      } else {
        let obj= {
          name: this.name,
          type: this.type,
          symbol: this.symbol,
          coinSymbol: this.coinSymbol,
          baseSymbol: this.baseSymbol,
          openFee: this.openFee,
          shareNumber: this.shareNumber,
          coinScale: this.coinScale,
          maintenanceMarginRate: this.maintenanceMarginRate,
          minShare: this.minShare,
          maxShare: this.maxShare,
          sort: this.sort,
          takerFee: this.takerFee,
          makerFee: this.makerFee
        }
        addContractCoin(obj).then( res => {
          if(!res.code) {
            this.$Notice.success({
                    title: "添加成功！",
                    desc: res.message,
                    duration: 10
                });
            this.current = 1;
            this.refreshdata(1);
          }else{
            this.$Notice.error({
                    title: "添加失败",
                    desc: res.message,
                    duration: 10
                });
          }
        })
      }
    },
    selected(selection) {
      this.deleteArr = [];
      selection.forEach(item => {
        this.deleteArr.push(item.symbol)
      });
    },
    confirmDel() {
      delBBSetting({ ids: this.deleteArr }).then( res =>{
        if(!res.code) {
          this.$Notice.success({
                    title: "删除成功！",
                    desc: res.message,
                    duration: 10
                });
          this.current = 1;
          this.refreshdata(1);
        }else {
          this.$Notice.error({
                    title: "删除失败！",
                    desc: res.message,
                    duration: 10
                });
        }
      })
    },
    refreshPageManual() {
      this.ifLoading = true;
      this.refreshdata(this.current);
    },
    changePage(pageIndex) {
      this.ifLoading = true;
      this.current = pageIndex;
      this.refreshdata(pageIndex);
    },
    search(){
      this.refreshdata(1);
    },
    refreshdata(pageIndex) {
      queryContractCoin({ pageNo: pageIndex, pageSize: 50, coinSymbol: this.searchSymbol, baseSymbol: this.searchBase }).then( res => {
        this.exchange = res.data.content;
        this.pageNum = res.data.totalElements;
        this.ifLoading = false;
      });
    },
    delcoin() {
      if(!this.deleteArr.length) {
        this.$Message.warning('尚未选取项目！');
      } else  this.ifDelete = true;
    },
    addcoin() {
      this.auditModal = true;
      this.name=null;
      this.symbol = null;
      this.enable = "1";
      this.openFee = null;
      this.coinScale = null;
      this.minShare = null;
      this.maxShare = null;
      this.shareNumber = null;
      this.maintenanceMarginRate = null;
      this.sort = null;
      this.takerFee = null;
      this.makerFee = null;
      this.visible = "1";
      this.exchangeable = "1";
      this.enableMarketBuy = "1";
      this.enableMarketSell = "1";
    },
  },
  created() {
    this.refreshdata(1);
  }
};
</script>

<style lang="less" scoped>
  .auditModel{
    ul {
      padding-left: 20px;
      li {
        position: relative;
        margin-bottom: 18px;
        span{
          position: absolute;
          top: 0;
          left: 0;
          display: inline-block;
          width: 95px;
          text-align: right;
          i{
            font-size: 14px;
            font-weight: 700;
            color: #ec0909;
          }
        }
        p{
          padding-left: 100px;
          min-width: 300px;
          line-height: 32px;
          em{
            position: static;
            color: unset;
          }
        }
      }
    }
  }
  .setting-title{
    font-size:14px;font-weight:bold;padding-bottom:20px;
  }
.auditModel ul li>em{
    position: absolute;
    bottom: -15px;
    font-size:10px;
    margin-left: 100px;
    line-height:12px;
    font-style: normal;
    color: #ec0909;
  }
</style>
