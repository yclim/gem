import React, { FunctionComponent, useEffect, useState } from "react";
import { RouteComponentProps } from "@reach/router";
import { Button, ButtonGroup } from "@blueprintjs/core";
import { Intent } from "@blueprintjs/core/lib/esm/common/intent";
import { File } from "./api";
import ruleService from "./api/mock";
import fileService from "./api/GEMFileAPIService";
import FileList from "./FileList";

const BrowseFiles: FunctionComponent<RouteComponentProps> = () => {
  const ALL = "All";
  const [currentType, setCurrentType] = useState<string>(ALL);
  const [types, setTypes] = useState<string[]>([]);
  const [files, setFiles] = useState<File[]>([]);

  useEffect(() => {
    ruleService.getFileTypes().then(t => {
      setTypes(t);
    });
  }, []);

  useEffect(() => {
    if(ALL === "All"){
        fileService.getFiles().then(f => {
          setFiles(f);
        });
    }else{
        fileService.getFilesByType(currentType).then(f => {
          setFiles(f);
        });
    }
  }, [currentType]);

  function renderTypeButton() {
    return types.map(t => (
      <Button
        key={t}
        intent={currentType === t ? Intent.PRIMARY : Intent.NONE}
        onClick={() => handleChangeType(t)}
      >
        {t}
      </Button>
    ));
  }

  function handleChangeType(type: string) {
    setCurrentType(type);
  }

  function handleSynchronize() {
    setCurrentType(type);
  }

  return (
    <div className="vertical-container">
      <div>
        <Button icon="refresh" text="Synchronize" onClick={()=> handleSynchronize} />
      </div>
      <div className="horizontal-container">
        <div className="left-nav-section">
          <ButtonGroup vertical={true}>
            <Button
              intent={currentType === ALL ? Intent.PRIMARY : Intent.NONE}
              onClick={() => handleChangeType(ALL)}
            >
              {" "}
              {ALL}{" "}
            </Button>
            {renderTypeButton()}
          </ButtonGroup>
        </div>
        <div className="stretch">
          <FileList files={files} setFiles={setFiles} />
        </div>
      </div>
    </div>
  );
};

export default BrowseFiles;
