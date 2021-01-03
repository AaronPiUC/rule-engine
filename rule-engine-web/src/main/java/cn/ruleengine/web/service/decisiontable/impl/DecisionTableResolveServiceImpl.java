package cn.ruleengine.web.service.decisiontable.impl;

import cn.ruleengine.core.condition.Operator;
import cn.ruleengine.core.decisiontable.*;
import cn.ruleengine.core.value.Value;
import cn.ruleengine.web.enums.EnableEnum;
import cn.ruleengine.web.service.ValueResolve;
import cn.ruleengine.web.service.decisiontable.DecisionTableResolveService;
import cn.ruleengine.web.store.entity.RuleEngineDecisionTable;
import cn.ruleengine.web.store.manager.RuleEngineDecisionTableManager;
import cn.ruleengine.web.vo.condition.ConfigValue;
import cn.ruleengine.web.vo.decisiontable.*;
import cn.ruleengine.web.vo.generalrule.DefaultAction;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author dingqianwen
 * @date 2020/7/16
 * @since 1.0.0
 */
@Service
public class DecisionTableResolveServiceImpl implements DecisionTableResolveService {

    @Resource
    private RuleEngineDecisionTableManager ruleEngineDecisionTableManager;
    @Resource
    private ValueResolve valueResolve;


    @Override
    public DecisionTable getDecisionTableById(Integer id) {
        RuleEngineDecisionTable ruleEngineDecisionTable = this.ruleEngineDecisionTableManager.getById(id);
        return this.decisionTableProcess(ruleEngineDecisionTable);
    }

    @Override
    public DecisionTable decisionTableProcess(RuleEngineDecisionTable ruleEngineDecisionTable) {
        TableData tableData = JSON.parseObject(ruleEngineDecisionTable.getTableData(), TableData.class);
        DecisionTable decisionTable = new DecisionTable();
        decisionTable.setId(ruleEngineDecisionTable.getId());
        decisionTable.setCode(ruleEngineDecisionTable.getCode());
        decisionTable.setName(ruleEngineDecisionTable.getName());
        decisionTable.setDescription(ruleEngineDecisionTable.getDescription());
        decisionTable.setWorkspaceId(ruleEngineDecisionTable.getWorkspaceId());
        decisionTable.setWorkspaceCode(ruleEngineDecisionTable.getWorkspaceCode());
        decisionTable.setStrategyType(DecisionTableStrategyType.getByValue(ruleEngineDecisionTable.getStrategyType()));
        List<CollConditionHeads> collConditionHeads = tableData.getCollConditionHeads();
        for (CollConditionHeads collConditionHead : collConditionHeads) {
            CollHead collHead = new CollHead();
            ConfigValue leftValue = collConditionHead.getLeftValue();
            collHead.setLeftValue(this.valueResolve.getValue(leftValue.getType(), leftValue.getValueType(), leftValue.getValue()));
            collHead.setOperator(Operator.getByName(collConditionHead.getSymbol()));
            decisionTable.addCollHead(collHead);
        }
        List<Rows> rows = tableData.getRows();
        for (Rows row : rows) {
            Row decisionTableRow = new Row();
            decisionTableRow.setPriority(row.getPriority());
            List<CollCondition> conditions = row.getConditions();
            for (CollCondition condition : conditions) {
                Value value = this.valueResolve.getValue(condition.getType(), condition.getValueType(), condition.getValue());
                decisionTableRow.addColl(new Coll(value));
            }
            Result result = row.getResult();
            Value value = this.valueResolve.getValue(result.getType(), result.getValueType(), result.getValue());
            decisionTableRow.setAction(value);
            decisionTable.addRow(decisionTableRow);
        }
        DefaultAction defaultAction = tableData.getCollResultHead().getDefaultAction();
        if (EnableEnum.ENABLE.getStatus().equals(defaultAction.getEnableDefaultAction())) {
            decisionTable.setDefaultActionValue(this.valueResolve.getValue(defaultAction.getType(), defaultAction.getValueType(), defaultAction.getValue()));
        }
        return decisionTable;
    }

}