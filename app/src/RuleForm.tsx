import React, {
  ChangeEvent,
  FunctionComponent,
  useEffect,
  useState
} from "react";
import { Parameter, Rule } from "./api";
import { InputGroup } from "@blueprintjs/core";

interface IProps {
  rule: Rule;
  setRule: (r: Rule) => void;
  groupName: string;
  handleAdd: () => void;
}

const RuleForm: FunctionComponent<IProps> = ({
  rule,
  setRule,
  groupName,
  handleAdd
}) => {
  const [param1, setParam1] = useState<string>("");
  const [param2, setParam2] = useState<string>("");
  const [param3, setParam3] = useState<string>("");

  useEffect(() => {
    setParam1("");
    setParam2("");
    setParam3("");
  }, [rule]);

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

  return (
    <div>
      <div className="dialog-body">
        <div className="dialog-input-group">
          <label className="dialog-label"> Rule Name </label>
          <InputGroup
            id="ruleName"
            className="dialog-input"
            value={rule.name}
            onChange={(e: ChangeEvent<HTMLInputElement>) => {
              rule.name = e.target.value;
              setRule(rule);
            }}
          />
        </div>
        {renderParamForm(rule)}
      </div>

      <div className="bp3-dialog-footer">
        <div className="bp3-dialog-footer-actions">
          <button
            type="submit"
            className="bp3-button bp3-intent-primary"
            onClick={e => handleAdd()}
          >
            Add to {groupName}
          </button>
        </div>
      </div>
    </div>
  );
};

export default RuleForm;
