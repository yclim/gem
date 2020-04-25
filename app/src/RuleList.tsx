import React, {
  ChangeEvent,
  FunctionComponent,
  useEffect,
  useState
} from "react";
import { Group, ParamDef, RuleDef } from "./api";
import ruleService from "./api/mock";
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

interface IProps {
  groups: Map<string, Group>;
  setGroups: (group: Map<string, Group>) => void;
}
const RuleList: FunctionComponent<IProps> = ({ groups, setGroups }) => {
  const [rules, setRules] = useState([] as RuleDef[]);
  const [isOpen, setIsOpen] = useState(false);
  const [currentRule, setCurrentRule] = useState<RuleDef | null>(null);
  const [currentGroup, setCurrentGroup] = useState<Group | null>(null);
  const [ruleName, setRuleName] = useState<string>("");
  const [param1, setParam1] = useState<string>("");
  const [param2, setParam2] = useState<string>("");
  const [param3, setParam3] = useState<string>("");

  useEffect(() => {
    if (currentRule && currentGroup) {
      setRuleName(getLabel(currentGroup, currentRule.alias));
      setParam1("");
    }
  }, [currentRule]);

  useEffect(() => {
    ruleService.getRules().then(r => {
      setRules(r);
    });
  }, []);

  function addRuleToGroup() {
    // ruleService.addRuleToGroup(gname, rid, rlabel).then(status => {
    //     ruleService.getGroups().then(groups => {
    //         setGroups([...groups]);
    //     })
    // })
    // if (ruleName && param1 && currentRule && currentGroup) {
    //   currentGroup.rules = [
    //     ...currentGroup.rules,
    //     { ruleId: currentRule.ruleId, label: ruleName, paramValues: [param1] }
    //   ];
    //   setGroups(new Map(groups.set(currentGroup.groupName, currentGroup)));
    // }
  }

  function handleOpen(gname: string, rid: string) {
    const grp = groups.get(gname);
    if (typeof grp !== "undefined") {
      const ruleDef = rules.find(r => r.ruleId === rid);
      if (typeof ruleDef !== "undefined") {
        setCurrentRule(ruleDef);
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

  function getLabel(group: Group, alias: string) {
    let counter = 1;
    while (true) {
      const label = alias + "-" + counter;
      const ruleInstance = group.rules.find(r => r.label === label);
      if (typeof ruleInstance === "undefined") {
        return label;
      } else {
        counter++;
      }
    }
  }

  function renderGroupMenu(rule: RuleDef) {
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

  function renderButton(rule: RuleDef) {
    return (
      <Popover
        key={rule.ruleId}
        position={Position.RIGHT_TOP}
        content={renderGroupMenu(rule)}
      >
        <Button rightIcon="plus" text={rule.ruleId} />
      </Popover>
    );
  }

  function getPlaceholder(type: string) {
    if (type === "regex") {
      return ".*[a-Z]+";
    } else if (type === "string") {
      return "string";
    } else if (type === "int") {
      return "123";
    } else if (type === "string_list") {
      return "aaa,bbb,ccc";
    } else {
      return "";
    }
  }

  function renderParamInput(
    p: ParamDef,
    r: RuleDef,
    paramValue: string,
    setParamValue: (e: string) => void
  ) {
    return (
      <div key={r.ruleId + "_" + p.label}>
        <div className="dialog-input-group">
          <label className="dialog-label">{p.label}</label>

          <InputGroup
            className="dialog-input"
            placeholder={getPlaceholder(p.type)}
            value={paramValue}
            onChange={(e: ChangeEvent<HTMLInputElement>) =>
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

  function renderParamForm(r: RuleDef) {
    const len = r.paramDefs.length;

    return (
      <div>
        {renderParamInput(r.paramDefs[0], r, param1, setParam1)}
        {len >= 2 ? (
          renderParamInput(r.paramDefs[1], r, param2, setParam2)
        ) : (
          <span />
        )}
        {len === 3 ? (
          renderParamInput(r.paramDefs[2], r, param3, setParam3)
        ) : (
          <span />
        )}
      </div>
    );
  }

  function renderDialogBody(r: RuleDef) {
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
        {rules
          .filter(r => r.target.startsWith("file"))
          .map(r => renderButton(r))}
        <h4> TIKA RULES</h4>
        <Divider />
        {rules
          .filter(r => r.target.startsWith("tika"))
          .map(r => renderButton(r))}
        <h4> CSV RULES</h4>
        <Divider />
        {rules
          .filter(r => r.target.startsWith("csv"))
          .map(r => renderButton(r))}
        <h4> EXCEL RULES</h4>
        <Divider />
        {rules
          .filter(r => r.target.startsWith("xls"))
          .map(r => renderButton(r))}
      </ButtonGroup>
      <Dialog
        isOpen={isOpen}
        icon="annotation"
        onClose={handleClose}
        title={`Edit ${currentRule ? currentRule.ruleId : "-"}`}
        transitionDuration={100}
      >
        {currentRule ? renderDialogBody(currentRule) : ""}
      </Dialog>
    </div>
  );
};

export default RuleList;
