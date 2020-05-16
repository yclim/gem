import React, {
  FunctionComponent,
  useContext,
  useEffect,
  useState
} from "react";
import { Group, Rule } from "./api";
import groupRuleService from "./api/GroupRuleService";
import {
  Alignment,
  Button,
  ButtonGroup,
  Dialog,
  Divider,
  Menu,
  MenuDivider,
  MenuItem,
  Popover,
  Position
} from "@blueprintjs/core";
import RuleForm from "./RuleForm";
import { StoreContext } from "./StoreContext";

interface IProps {
  setNewGroupRuleName: (g: string | null) => void;
}

const RuleList: FunctionComponent<IProps> = ({ setNewGroupRuleName }) => {
  const context = useContext(StoreContext);

  const [rules, setRules] = useState([] as Rule[]);
  const [isOpen, setIsOpen] = useState(false);
  const [currentRule, setCurrentRule] = useState<Rule | null>(null);
  const [currentGroup, setCurrentGroup] = useState<Group | null>(null);

  useEffect(() => {
    groupRuleService.getRules().then(r => {
      setRules(r.data);
    });
  }, []);

  function addRuleToGroup() {
    if (currentRule && currentGroup) {
      context.groupsAction?.addGroupRule(currentGroup.name, currentRule);
      setNewGroupRuleName(currentRule.name);
      setCurrentRule(null);
    }
  }

  function handleOpen(gname: string, rid: string) {
    if (context.groupsState) {
      const grp = context.groupsState.get(gname);
      if (typeof grp !== "undefined") {
        const rule = rules.find(r => r.ruleId === rid);
        if (typeof rule !== "undefined") {
          rule.name = generateLabel(rule);
          // clone the currentRule so that it will not modify parameters value in rules
          setCurrentRule(JSON.parse(JSON.stringify(rule)));
          setIsOpen(true);
          setCurrentGroup(grp);
        }
      }
    }
  }

  function handleClose() {
    setIsOpen(false);
  }

  function handleAdd() {
    addRuleToGroup();
    handleClose();
  }

  /**
   * generate rule name base on rule label
   * take first letter of each word in the label and add counter suffix because rule name must be unique
   * e.g Filename Extension -> FE-1
   */
  function generateLabel(rule: Rule) {
    if (rule.label) {
      let counter = 1;
      const alias = rule.label
        .split(" ")
        .map(word => word[0])
        .join("");
      while (true) {
        const finalName = alias + "-" + counter;
        if (!context.groupsAction?.isRuleNameUsed(finalName)) {
          return finalName;
        } else {
          counter++;
        }
      }
    } else {
      alert("illegal state: rule label is undefined");
      return "undefined";
    }
  }

  function renderGroupMenu(rule: Rule) {
    return (
      <Menu>
        <MenuDivider title="Add to Group" />
        {Array.from(context.groupsState, ([k, v]) => v).map(g => (
          <MenuItem
            key={g.name}
            text={g.name}
            icon="group-objects"
            onClick={() => {
              handleOpen(g.name, rule.ruleId);
            }}
          />
        ))}
      </Menu>
    );
  }

  function renderButton(rule: Rule) {
    return (
      <Popover
        key={rule.ruleId}
        position={Position.RIGHT_TOP}
        content={renderGroupMenu(rule)}
      >
        <Button rightIcon="plus" text={rule.label} />
      </Popover>
    );
  }

  return (
    <div className="box">
      <ButtonGroup
        vertical={true}
        alignText={Alignment.LEFT}
        large={false}
        minimal={true}
      >
        <h4> FILE DETAIL RULES</h4>
        <Divider />
        {rules.filter(r => r.ruleType === "FILE").map(r => renderButton(r))}
        <h4> TIKA RULES</h4>
        <Divider />
        {rules
          .filter(r => r.ruleType?.startsWith("TIKA"))
          .map(r => renderButton(r))}
        <h4> CSV RULES</h4>
        <Divider />
        {rules.filter(r => r.ruleType === "CSV").map(r => renderButton(r))}
        <h4> EXCEL RULES</h4>
        <Divider />
        {rules.filter(r => r.ruleType === "EXCEL").map(r => renderButton(r))}
      </ButtonGroup>
      <Dialog
        isOpen={isOpen}
        icon="annotation"
        onClose={handleClose}
        title={`${currentRule ? currentRule.label : "-"}`}
        transitionDuration={100}
      >
        {currentRule && currentGroup ? (
          <RuleForm
            rule={currentRule}
            setRule={setCurrentRule}
            groupName={currentGroup.name}
            handleSubmit={handleAdd}
          />
        ) : (
          ""
        )}
      </Dialog>
    </div>
  );
};

export default RuleList;
