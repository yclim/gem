import React, {ChangeEvent, FunctionComponent, useEffect, useState} from "react";
import { RouteComponentProps } from "@reach/router";
import groupRuleService from "./api/GroupRuleService";
import {ProjectSpec} from "./api";
import {
    AnchorButton,
    Button,
    Card,
    Elevation, FileInput,
    FormGroup,
    InputGroup
} from "@blueprintjs/core";
import extractConfigService from "./api/ExtractConfigService";
import {AxiosResponse} from "axios";
import {Group} from "./api";

const ExportSpec: FunctionComponent<RouteComponentProps> = () => {
    const [projectName, setProjectName] = useState<string>("");
    const [projectVersion, setProjectVersion] = useState<string>("1.0");
    const [projectSpec, setProjectSpec] = useState<ProjectSpec>({});

    useEffect(() => {
        groupRuleService.getGroupsSpec()
            .then((resp: AxiosResponse<ProjectSpec>) => {
                const specs = resp.data
                specs.specVersion = projectVersion
                specs.projectName = projectName
                setProjectSpec(specs)
            });
    }, []);

    function handleProjectNameChange(name: string) {
        setProjectName(name)
        const specs = projectSpec
        specs.projectName = name
        setProjectSpec(specs)
    }
    function handleVersionChange(version: string) {
        setProjectVersion(version)
        const specs = projectSpec
        specs.specVersion = version
        setProjectSpec(specs)
    }

    function handleFileSelected(event: ChangeEvent<HTMLInputElement>) {
        const selectedFile = event.target.files[0];
        const data = new FormData();
        data.append("file", selectedFile)

        groupRuleService.importGroupsFile(data).then(response => {
            console.log(response.data)
        });
    }
    return (
        <div className="grid2">
            <Card>
                <FormGroup label="Project Name" labelFor="text-input1">
                    <InputGroup
                        id="text-input1"
                        placeholder="My Project Name"
                        onChange={(e: ChangeEvent<HTMLInputElement>) =>
                            handleProjectNameChange(e.target.value)
                        }
                    />
                </FormGroup>
                <FormGroup label="Project Version" labelFor="text-input2">
                    <InputGroup
                        id="text-input2"
                        placeholder="1.0"
                        value={projectVersion}
                        onChange={(e: ChangeEvent<HTMLInputElement>) =>
                            handleVersionChange(e.target.value)
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
                    <AnchorButton
                        text="Export Spec"
                        icon="export"
                        className="add-right-margin"
                        href={"/api/group/export?name="+projectName+"&version="+projectVersion}
                    />

                    <FileInput
                        text="Choose spec file..."
                        buttonText="Import"
                        onInputChange={(e: React.ChangeEvent<HTMLInputElement>) =>
                            handleFileSelected(e)
                        }
                    />
                </div>
            </Card>
            <Card elevation={Elevation.ZERO} className="detail-box">
        <pre>
          {JSON.stringify(
              projectSpec,
              null,
              2
          )}
        </pre>
            </Card>
        </div>
    );
};

export default ExportSpec;
