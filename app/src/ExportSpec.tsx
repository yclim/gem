import React, { ChangeEvent, FunctionComponent, useState } from "react";
import { RouteComponentProps } from "@reach/router";
import {
  Button,
  Card,
  Elevation,
  FormGroup,
  InputGroup
} from "@blueprintjs/core";

const ExportSpec: FunctionComponent<RouteComponentProps> = () => {
  const [projectName, setProjectName] = useState<string>("");
  const [projectVersion, setProjectVersion] = useState<string>("1.0");
  return (
    <div className="grid2">
      <Card>
        <FormGroup label="Project Name" labelFor="text-input1">
          <InputGroup
            id="text-input1"
            placeholder="My Project Name"
            onChange={(e: ChangeEvent<HTMLInputElement>) =>
              setProjectName(e.target.value)
            }
          />
        </FormGroup>
        <FormGroup label="Project Version" labelFor="text-input2">
          <InputGroup
            id="text-input2"
            placeholder="1.0"
            value={projectVersion}
            onChange={(e: ChangeEvent<HTMLInputElement>) =>
              setProjectVersion(e.target.value)
            }
          />
        </FormGroup>
        <FormGroup label="Metadata 1" labelFor="text-input3">
          <InputGroup id="text-input3" placeholder="meta1" />
        </FormGroup>
        <FormGroup label="Metadata 2" labelFor="text-input4">
          <InputGroup id="text-input4" placeholder="meta2" />
        </FormGroup>

        <div>
          <Button
            text="Export Spec"
            icon="export"
            className="add-right-margin"
          />
          <Button
            text="Import Spec"
            icon="import"
            className="add-right-margin"
          />
        </div>
      </Card>
      <Card elevation={Elevation.ZERO} className="detail-box">
        <pre>
          {JSON.stringify(
            {
              specificationVersion: "1.0",
              project: projectName,
              projectVersion,
              groups: [
                { name: "grp1", rules: [{ name: "rule1" }] },
                { name: "grp2", rules: [{ name: "rule1" }] },
                { name: "grp3", rules: [{ name: "rule1" }] }
              ]
            },
            null,
            2
          )}
        </pre>
      </Card>
    </div>
  );
};

export default ExportSpec;
