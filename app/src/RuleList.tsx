import React, {
  ChangeEvent,
  Dispatch,
  FunctionComponent,
  useEffect,
  useState
} from "react";
import { Group, Parameter, Rule } from "./api";
import groupRuleService from "./api/GroupRuleService";
import {
  Alignment,
  Button,
  ButtonGroup,
  Dialog,
  Divider,
  Icon,
  InputGroup,
  Intent,
  Menu,
  MenuDivider,
  MenuItem,
  Popover,
  Position
} from "@blueprintjs/core";
import { GroupAction, GroupActions, rulenameExist } from "./EditGroups";

interface IProps {
  groups: Map<string, Group>;
  groupDispatcher: Dispatch<GroupAction>;
  setFocusedGroupRuleName: (g: string | null) => void;
}
const RuleList: FunctionComponent<IProps> = ({
  groups,
  groupDispatcher,
  setFocusedGroupRuleName
}) => {
  const [rules, setRules] = useState([] as Rule[]);
  const [isOpen, setIsOpen] = useState(false);
  const [currentRule, setCurrentRule] = useState<Rule | null>(null);
  const [currentGroup, setCurrentGroup] = useState<Group | null>(null);
  const [ruleName, setRuleName] = useState<string>("");
  const [param1, setParam1] = useState<string>("");
  const [param2, setParam2] = useState<string>("");
  const [param3, setParam3] = useState<string>("");

  useEffect(() => {
    groupRuleService.getRules().then(r => {
      setRules(r.data);
    });
  }, []);

  useEffect(() => {
    if (currentRule && groups) {
      setRuleName(generateLabel(currentRule));
      setParam1("");
    }
  }, [currentRule]);

  function addRuleToGroup() {
    if (ruleName && param1 && currentRule && currentGroup) {
      groupDispatcher(
        GroupActions.addGroupRule({
          groupName: currentGroup.name,
          ruleName,
          ruleId: currentRule.ruleId,
          ruleParams: [param1]
        })
      );
      setFocusedGroupRuleName(ruleName);
      setCurrentRule(null);
    }
  }

  function handleOpen(gname: string, rid: string) {
    const grp = groups.get(gname);
    if (typeof grp !== "undefined") {
      const rule = rules.find(r => r.ruleId === rid);
      if (typeof rule !== "undefined") {
        setCurrentRule(rule);
        setIsOpen(true);
        setCurrentGroup(grp);
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
        if (!rulenameExist(groups, finalName)) {
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
        {Array.from(groups, ([k, v]) => v).map(g => (
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

  function renderParamInput(
    p: Parameter,
    r: Rule,
    paramValue: string,
    setParamValue: (e: string) => void
  ) {
    return (
      <div key={r.name}>
        <div className="dialog-input-group">
          <label className="dialog-label">{p.label}</label>
          <InputGroup
            className="dialog-input"
            placeholder={p.placeholder}
            value={paramValue}
            onChange={(e: ChangeEvent<HTMLInputElement>) =>
              // TODO: handle multiple params
              setParamValue(e.target.value)
            }
            rightElement={
              <Icon
                className="dialog-input-icon"
                icon="tick"
                intent={Intent.SUCCESS}
              />
            }
          />
        </div>
      </div>
    );
  }

  function renderParamForm(r: Rule) {
    const len = r.params.length;

    return (
      <div>
        {renderParamInput(r.params[0], r, param1, setParam1)}
        {len >= 2 ? (
          renderParamInput(r.params[1], r, param2, setParam2)
        ) : (
          <span />
        )}
        {len === 3 ? (
          renderParamInput(r.params[2], r, param3, setParam3)
        ) : (
          <span />
        )}
      </div>
    );
  }

  function renderDialogBody(r: Rule) {
    if (r !== null) {
      return (
        <div>
          <div className="dialog-body">
            <div className="dialog-input-group">
              <label className="dialog-label"> Rule Name </label>
              <InputGroup
                id="ruleName"
                className="dialog-input"
                value={ruleName}
                onChange={(e: ChangeEvent<HTMLInputElement>) =>
                  setRuleName(e.target.value)
                }
                rightElement={
                  <Icon
                    className="dialog-input-icon"
                    icon="tick"
                    intent={Intent.SUCCESS}
                  />
                }
              />
            </div>
            {renderParamForm(r)}
          </div>

          <div className="bp3-dialog-footer">
            <div className="bp3-dialog-footer-actions">
              <button
                type="submit"
                className="bp3-button bp3-intent-primary"
                onClick={e => handleAdd()}
              >
                Add to {currentGroup ? currentGroup.name : ""}
              </button>
            </div>
          </div>
        </div>
      );
    } else {
      return <div>nothing</div>;
    }
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
        {rules.filter(r => r.ruleType === "TIKA").map(r => renderButton(r))}
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
        {currentRule ? renderDialogBody(currentRule) : ""}
      </Dialog>
    </div>
  );
};

export default RuleList;
