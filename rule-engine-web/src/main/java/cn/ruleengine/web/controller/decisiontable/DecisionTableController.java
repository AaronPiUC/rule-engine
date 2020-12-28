package cn.ruleengine.web.controller.decisiontable;

import cn.ruleengine.web.annotation.DataPermission;
import cn.ruleengine.web.annotation.ReSubmitLock;
import cn.ruleengine.web.enums.DataPermissionType;
import cn.ruleengine.web.enums.PermissionType;
import cn.ruleengine.web.service.decisiontable.DecisionTableService;
import cn.ruleengine.web.vo.base.request.IdRequest;
import cn.ruleengine.web.vo.base.request.PageRequest;
import cn.ruleengine.web.vo.base.request.Param;
import cn.ruleengine.web.vo.base.response.BaseResult;
import cn.ruleengine.web.vo.base.response.PageResult;
import cn.ruleengine.web.vo.base.response.PlainResult;
import cn.ruleengine.web.vo.decisiontable.DecisionTableDefinition;
import cn.ruleengine.web.vo.decisiontable.ListDecisionTableRequest;
import cn.ruleengine.web.vo.decisiontable.ListDecisionTableResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author 丁乾文
 * @create 2020/12/27
 * @since 1.0.0
 */
@Api(tags = "决策表控制器")
@RestController
@RequestMapping("ruleEngine/decisionTable")
public class DecisionTableController {

    @Resource
    private DecisionTableService decisionTableService;

    /**
     * 决策表列表
     *
     * @param pageRequest 分页查询参数
     * @return page
     */
    @PostMapping("list")
    @ApiOperation("决策表列表")
    public PageResult<ListDecisionTableResponse> list(@RequestBody PageRequest<ListDecisionTableRequest> pageRequest) {
        return this.decisionTableService.list(pageRequest);
    }

    /**
     * 保存或者更新决策表定义信息
     *
     * @param decisionTableDefinition 定义信息
     * @return 决策表id
     */
    @ReSubmitLock
    @DataPermission(id = "#decisionTableDefinition.id", dataType = DataPermissionType.DECISION_TABLE, type = PermissionType.VALID_WORKSPACE)
    @PostMapping("saveOrUpdateDecisionTableDefinition")
    @ApiOperation("保存或者更新决策表定义信息")
    public BaseResult saveOrUpdateDecisionTableDefinition(@Valid @RequestBody DecisionTableDefinition decisionTableDefinition) {
        PlainResult<Integer> plainResult = new PlainResult<>();
        plainResult.setData(this.decisionTableService.saveOrUpdateDecisionTableDefinition(decisionTableDefinition));
        return plainResult;
    }

    /**
     * 查询决策表定义信息
     *
     * @param idRequest 决策表id
     * @return DecisionTableDefinition
     */
    @DataPermission(id = "#idRequest.id", dataType = DataPermissionType.DECISION_TABLE, type = PermissionType.VALID_WORKSPACE)
    @PostMapping("getDecisionTableDefinition")
    @ApiOperation("查询决策表定义信息")
    public BaseResult getDecisionTableDefinition(@Valid @RequestBody IdRequest idRequest) {
        PlainResult<DecisionTableDefinition> plainResult = new PlainResult<>();
        plainResult.setData(this.decisionTableService.getDecisionTableDefinition(idRequest.getId()));
        return plainResult;
    }

    /**
     * 决策表Code是否存在
     *
     * @param param 决策表code
     * @return true 存在
     */
    @PostMapping("codeIsExists")
    @ApiOperation("决策表Code是否存在")
    public PlainResult<Boolean> codeIsExists(@RequestBody @Valid Param<String> param) {
        PlainResult<Boolean> plainResult = new PlainResult<>();
        plainResult.setData(this.decisionTableService.decisionTableCodeIsExists(param.getParam()));
        return plainResult;
    }

    /**
     * 删除决策表
     *
     * @param idRequest 决策表id
     * @return true
     */
    @DataPermission(id = "#idRequest.id", dataType = DataPermissionType.DECISION_TABLE, type = PermissionType.DELETE)
    @PostMapping("delete")
    @ApiOperation("删除决策表")
    public BaseResult delete(@Valid @RequestBody IdRequest idRequest) {
        PlainResult<Boolean> plainResult = new PlainResult<>();
        plainResult.setData(this.decisionTableService.delete(idRequest.getId()));
        return plainResult;
    }

}
