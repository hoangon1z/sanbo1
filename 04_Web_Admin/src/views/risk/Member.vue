<template>
  <div>
    <Row>
      <Card>
        <p slot="title">
          会员收益
          <Button type="primary" size="small" @click="refreshPageManual">
            <Icon type="refresh"></Icon>刷新
          </Button>
        </p>
        <MemberList  ref="memberSelect" :search-able="true"/>
      </Card>
    </Row>
  </div>
</template>

<script>
import MemberList from "./components/MemberList";
export default {
  components: {
    MemberList
  },  
  data() {
    return {
      
    }
  },
  methods: {
    refreshPageManual() {
      this.$refs['memberSelect'].refreshPageManual();
    }
  },
  created() {
  },
};
</script>

<style scoped>
.ivu-upload {
  display: inline-block;
}
</style>
