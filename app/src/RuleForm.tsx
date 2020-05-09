import React, {
  ChangeEvent,
  FunctionComponent,
  useEffect,
  useState
} from "react";
import { Parameter, Rule } from "./api";
import { FormGroup, InputGroup } from "@blueprintjs/core";
import { Intent } from "@blueprintjs/core/lib/esm/common/intent";

interface IProps {
  rule: Rule;
  setRule: (r: Rule) => void;
  groupName: string;
  handleSubmit: () => void;
}

const RuleForm: FunctionComponent<IProps> = ({
  rule,
  setRule,
  groupName,
  handleSubmit
}) => {
  const [param1, setParam1] = useState<string | null>(
    rule.params.length > 0 ? rule.params[0].value : ""
  );
  const [param2, setParam2] = useState<string | null>(
    rule.params.length > 1 ? rule.params[1].value : ""
  );
  const [param3, setParam3] = useState<string | null>(
    rule.params.length > 2 ? rule.params[2].value : ""
  );
  const [param1Missing, setParam1Missing] = useState<boolean>(false);
  const [param2Missing, setParam2Missing] = useState<boolean>(false);
  const [param3Missing, setParam3Missing] = useState<boolean>(false);

  function handleTextChange(
    text: string,
    setParam: (s: string) => void,
    setParamMissing: (b: boolean) => void
  ) {
    if (!text || text.trim() === "") {
      setParamMissing(true);
    }
    setParam(text);
  }

  function renderParamInput(
    p: Parameter,
    r: Rule,
    paramValue: string | null,
    setParamValue: (e: string) => void,
    paramMissing: boolean,
    setParamMissing: (e: boolean) => void
  ) {
    return (
      <div key={p.label}>
        <FormGroup label={p.label} labelFor={p.label}>
          <InputGroup
            id={p.label}
            placeholder={p.placeholder}
            value={paramValue ? paramValue : ""}
            onChange={(e: ChangeEvent<HTMLInputElement>) =>
              handleTextChange(e.target.value, setParamValue, setParamMissing)
            }
            intent={
              paramMissing && (!paramValue || paramValue === "")
                ? Intent.DANGER
                : Intent.NONE
            }
            style={p.type === "REGEX" ? { fontFamily: "consolas" } : {}}
          />
        </FormGroup>
      </div>
    );
  }

  function renderParamForm(r: Rule) {
    const len = r.params.length;
    return (
      <div>
        {renderParamInput(
          r.params[0],
          r,
          param1,
          setParam1,
          param1Missing,
          setParam1Missing
        )}
        {len >= 2 ? (
          renderParamInput(
            r.params[1],
            r,
            param2,
            setParam2,
            param2Missing,
            setParam2Missing
          )
        ) : (
          <span />
        )}
        {len === 3 ? (
          renderParamInput(
            r.params[2],
            r,
            param3,
            setParam3,
            param3Missing,
            setParam3Missing
          )
        ) : (
          <span />
        )}
      </div>
    );
  }

  function handleFormSubmit() {
    if (isPresent([rule.name])) {
      if (rule.params.length === 1 && isPresent([param1])) {
        if (param1) {
          rule.params[0].value = param1;
          setRule(rule);
          handleSubmit();
          reset();
        }
      } else {
        setParam1Missing(true);
      }

      if (rule.params.length === 2 && isPresent([param1, param2])) {
        if (param1 && param2) {
          rule.params[0].value = param1;
          rule.params[1].value = param2;
          setRule(rule);
          handleSubmit();
          reset();
        }
      } else {
        if (!isPresent([param1])) setParam1Missing(true);
        if (!isPresent([param2])) setParam2Missing(true);
      }

      if (rule.params.length === 3 && isPresent([param1, param2, param3])) {
        if (param1 && param2 && param3) {
          rule.params[0].value = param1;
          rule.params[1].value = param2;
          rule.params[2].value = param3;
          setRule(rule);
          handleSubmit();
          reset();
        }
      } else {
        if (!isPresent([param1])) setParam1Missing(true);
        if (!isPresent([param2])) setParam2Missing(true);
        if (!isPresent([param3])) setParam3Missing(true);
      }
    }

    function isPresent(strs: (string | null)[]): boolean {
      return strs.every(str => str && str.trim() !== "");
    }
  }

  function reset() {
    setParam1("");
    setParam2("");
    setParam3("");
    setParam1Missing(false);
    setParam2Missing(false);
    setParam3Missing(false);
  }

  return (
    <div>
      <div className="dialog-body">
        <FormGroup label="Rule Name" labelFor="rulename-input">
          <InputGroup
            id="rulename-input"
            placeholder="rule name"
            value={rule.name}
            onChange={(e: ChangeEvent<HTMLInputElement>) => {
              rule.name = e.target.value;
              setRule({ ...rule, name: e.target.value });
            }}
            intent={
              !rule.name || rule.name.trim() === ""
                ? Intent.DANGER
                : Intent.NONE
            }
          />
        </FormGroup>
        {renderParamForm(rule)}
      </div>

      <div className="bp3-dialog-footer">
        <div className="bp3-dialog-footer-actions">
          <button
            type="submit"
            className="bp3-button bp3-intent-primary"
            onClick={e => handleFormSubmit()}
          >
            Add to {groupName}
          </button>
        </div>
      </div>
    </div>
  );
};

export default RuleForm;
