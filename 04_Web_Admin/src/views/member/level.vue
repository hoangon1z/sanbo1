<template>
  <div>
      <Row>
          <Card>
                <p slot="title">
                会员等级管理
                    <Button type="primary" size="small" @click="refreshPageManual">
                        <Icon type="refresh"></Icon>刷新
                    </Button>
                </p>
                <div style="margin-bottom : 10px">
                <!-- <Button type="success" size="big" @click="handleAddLevel">
                        <Icon type="add"></Icon>新增会员等级
                </Button>  -->
                </div>
                <Row>
                    <Table
                        :columns="columns"
                        :data="orderList"
                        border
                        :loading="ifLoading"
                        >
                    </Table>
                </Row>
                <div class="pageWrapper">
                    <Page
                        :total="pageNum"
                        :current="currentPageIdx"
                        @on-change="changePage"
                        show-elevator>
                    </Page>
                </div> 
                
                      <Modal
          class="auditModel"
          v-model="addModal"
          title="新增会员等级"
          
          @on-ok="handleSubmit"
          >  
        <i-form :model="addformItem" :label-width="140">
        <Form-item label="会员等级"  prop="id">
            <i-input  v-model="addformItem.id"   placeholder="会员等级不可以重复"></i-input>
        </Form-item>  
        <Form-item label="会员等级名"  prop="name">
            <i-input  v-model="addformItem.name"   placeholder="请输入会员等级名"></i-input>
        </Form-item>  
        <Form-item label="备注"  prop="remark">
            <i-input  v-model="addformItem.remark"   placeholder="请输入备注"></i-input>
        </Form-item>
        <Form-item label="折扣"  prop="discount" >
            <i-input  v-model="addformItem.discount" width="40" placeholder="请输入折扣"></i-input>
            <span style=" position: absolute; top: 1%; right: 6%;color: #adadad; display: table-cell;white-space: nowrap; padding: 3px 10px;">折</span>
        </Form-item>
        <Form-item label="升级需直属好友数"  prop="extTerm1">
            <i-input  v-model="addformItem.extTerm1"   placeholder="请输入升级需直属好友数"></i-input>
          
        </Form-item>
        <Form-item label="升级需直属好友等级"  prop="extTerm2">
            <i-input  v-model="addformItem.extTerm2"   placeholder="请输入升级需直属好友等级"></i-input>
           
        </Form-item>
        <Form-item label="升级需团队算力"  prop="extTerm3">
            <i-input  v-model="addformItem.extTerm3"   placeholder="请输入升级需团队算力"></i-input>
           
        </Form-item>
        <Form-item label="众筹项目限额"  prop="extRight1">
            <i-input  v-model="addformItem.extRight1"   placeholder="请输入众筹项目限额"></i-input>
            <span style=" position: absolute; top: 1%; right: 6%;color: #adadad; display: table-cell;white-space: nowrap; padding: 3px 10px;">万(USDT)</span>
        </Form-item>
        <Form-item label="线下活动补助"  prop="extRight2">
            <i-input  v-model="addformItem.extRight2"   placeholder="请输入线下活动补助"></i-input>
            <span style=" position: absolute; top: 1%; right: 6%;color: #adadad; display: table-cell;white-space: nowrap; padding: 3px 10px;">万(USDT)</span>
        </Form-item>
        <!-- <Form-item label="权益3"  prop="extRight3">
            <i-input  v-model="addformItem.extRight3"   placeholder="请输入权益3"></i-input>
            <span style=" position: absolute; top: 1%; right: 6%;color: #adadad; display: table-cell;white-space: nowrap; padding: 3px 10px;">万</span>
        </Form-item> -->
         </i-form>
      </Modal>
           <Modal
          class="auditModel"
          v-model="editModal"
          title="编辑会员等级"
          @on-ok="handleEdit"
          >  
        <i-form :model="editForm" :label-width="140">
        <Form-item label="会员等级"  prop="id">
            <i-input  v-model="editForm.id"  disabled  placeholder="会员等级不可以重复"></i-input>
        </Form-item>  
        <Form-item label="会员等级名"  prop="name">
            <i-input  v-model="editForm.name"   placeholder="请输入会员等级名"></i-input>
        </Form-item>  
        <Form-item label="备注"  prop="remark">
            <i-input  v-model="editForm.remark"   placeholder="请输入备注"></i-input>
        </Form-item>
        <Form-item label="折扣"  prop="discount" >
            <i-input  v-model="editForm.discount" width="40" placeholder="请输入折扣"></i-input>
            <span style=" position: absolute; top: 1%; right: 6%;color: #adadad; display: table-cell;white-space: nowrap; padding: 3px 10px;">折</span>
        </Form-item>
        <Form-item label="升级需直属好友数"  prop="extTerm1">
            <i-input  v-model="editForm.remark"   placeholder="请输入升级需直属好友数"></i-input>
          
        </Form-item>
        <Form-item label="升级需直属好友等级"  prop="extTerm2">
            <i-input  v-model="editForm.extTerm2"   placeholder="请输入升级需直属好友等级"></i-input>
           
        </Form-item>
        <Form-item label="升级需团队算力"  prop="extTerm3">
            <i-input  v-model="editForm.extTerm3"   placeholder="请输入升级需团队算力"></i-input>
           
        </Form-item>
        <Form-item label="众筹项目限额"  prop="extRight1">
            <i-input  v-model="editForm.extRight1"   placeholder="请输入众筹项目限额"></i-input>
            <span style=" position: absolute; top: 1%; right: 6%;color: #adadad; display: table-cell;white-space: nowrap; padding: 3px 10px;">万(USDT)</span>
        </Form-item>
        <Form-item label="线下活动补助"  prop="extRight2">
            <i-input  v-model="editForm.extRight2"   placeholder="请输入线下活动补助"></i-input>
            <span style=" position: absolute; top: 1%; right: 6%;color: #adadad; display: table-cell;white-space: nowrap; padding: 3px 10px;">万(USDT)</span>
        </Form-item>
        <!-- <Form-item label="权益3"  prop="extRight3">
            <i-input  v-model="editForm.extRight3"   placeholder="请输入权益3"></i-input>
            <span style=" position: absolute; top: 1%; right: 6%;color: #adadad; display: table-cell;white-space: nowrap; padding: 3px 10px;">万</span>
        </Form-item> -->
         </i-form>
      </Modal>

          </Card>
      </Row>


  </div>
</template>

<script>

  import { getAllLevel, updateDefault, addLevel, updateLevel } from '@/service/getData';
 
  export default{
    data () {
      const self = this;
      return {
        addformItem:{},                  
        addModal:false,
        btnEvent: "",
        btnEventString: "",
        loginPW: '',        
        filterSearch: {
            pageNo: 1,
            pageSize: 10
        },
        editModal:false,
        editForm:{},
        detailOrder: {},
        currentPageIdx: 1,
        ifLoading: true,
        pageNum: null,
        orderList: [],
        columns: [
          {
            type: "index",
            width: 30,
            render: (h, params) => {
              return h(expandRow, {
                props: {
                  skin: params.row.skin,
                  rows: params.row.detail
                }
              });
            }
          },
          {
            title: "会员等级id",
            key: "id",
            minWidth: 100,
          },
          {
            title: "会员等级名",
            key: "name",
            minWidth: 100,
          },
          // {
          //   title: "备注",
          //   key: "remark",
          //   minWidth: 155,
          // },
          {
            title: "是否为默认等级",
            key: "isDefault",
            minWidth: 100,
            render: (h, params) => {
              let sta = "是";
              if(params.row.isDefault == 1 ){
                return h("span", {}, sta)
              }
              let no ="否"
              if(params.row.isDefault == 0 ){
                return h("div", {},no);
              }
            }
          },
          {
            title: "折扣",
            key: "discount",
            minWidth: 70,
          },
          {
            title: "升级需直属好友数",
            key: "extTerm1",
            minWidth: 115,
          },
            {
            title: "升级需直属好友等级",
            key: "extTerm2",
            minWidth: 115,
          },
            {
            title: "升级需团队算力",
            key: "extTerm3",
            minWidth: 115,
          },
            {
            title: "众筹项目限额",
            key: "extRight1",
            minWidth: 115,
          },
            {
            title: "线下活动补助",
            key: "extRight2",
            minWidth: 115,
          },
          //   {
          //   title: "线下活动补",
          //   key: "extRight2",
          //   minWidth: 115,
          // },
         {
          title: "操作",
          width: 200,
          render: (h, obj) => {
            
            var actions = [];
            if(obj.row.isDefault == false){
             actions.push(h(
                "Button",
                {
                  props: {
                    type: "success",
                    size: "small",
                    
                  },
                   style:{   
                    margin:' 10px',
 
                  },
                  on: {
                    click: () => {
                      this.handleUpDefaut(obj.row.id)
                    }
                  }
                },
                "设为默认"));
            }
            
              actions.push(h(
                "Button",
                {
                  props: {
                    type: "primary",
                    size: "small"
                  },
                  on: {
                    click: () => {
                      console.log(this)
                      this.handleupdate(obj.row)
                    }
                  }
                },
                "编辑"));
            return h("div",
             actions);
          }
        }
        ]
      };
    },



    methods: {
                        handleupdate(row){
                              this.editForm = row               
                              this.editModal = true;
                        },
                        
                      
                        
      refreshPageManual() {
                this.currentPageIdx = 1;
                for(let key in this.filterSearch) {
                    this.filterSearch[key] = '';
                }
                this.filterSearch.pageNo = 1;
                this.filterSearch.pageSize = 10;
                this.refreshPage(this.filterSearch);
      },
      changePage(pageIndex) {
                this.currentPageIdx = pageIndex;
                this.filterSearch.pageNo = pageIndex;
                this.refreshPage(this.filterSearch)
      },
      handleAddLevel(){
           this.addModal = true;
 
      },
      handleUpDefaut(id){
             updateDefault({id:id}).then(res =>{ 
                   this.$Message.success("设置成功")
                   this.refreshPage();         
             }).catch(err =>{
                  this.$Message.error(res.message)
             })          
          

      },
     
      refreshPage(obj) {
        this.ifLoading = true;
        getAllLevel(this.filterSearch).then( res => {
          if(!res.code) {
                this.ifLoading = false;
                this.pageNum = (res.data && res.data.totalElements) || 1;
                this.orderList = (res.data && res.data.content) || [];
          }else{
                this.$Message.error(res.message)
          }
        })
      },
      closeDetail(){
        this.showDetailModal = false;
      },
       handleSubmit(){
             addLevel(this.addformItem).then(res =>{                
                  if(res.code==500){
                      this.$Message.error(res.message)
                      this.addModal=true   
                      this.refreshPage();
                  } 
                  else{
                       this.$Message.success("新增成功") 
                       this.refreshPage();
                  }              
             }).catch(res =>{
                      this.$Message.error(res.message)   
             })              
             
      },
      handleEdit(){
            updateLevel(this.editForm).then(res =>{
                  if(res.code==500){
                      this.$Message.error(res.message)
                      this.editModal=true   
                      this.refreshPage();
                  } 
                  else{
                       this.$Message.success("编辑成功") 
                       this.refreshPage();
                  } 



            })              



      }
     
    },
    created () {
      this.refreshPage();
      
    }
  }
</script>

<style scoped>
  .ivu-upload{
    display: inline-block;
  }
</style>
