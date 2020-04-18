import React, { FunctionComponent, useEffect, useState } from "react";
import { RouteComponentProps } from "@reach/router";
import { Button, ButtonGroup, EditableText, Spinner } from "@blueprintjs/core";
import { Intent } from "@blueprintjs/core/lib/esm/common/intent";
import { File } from "./api";
import fileService from "./api/FileService";
import FileList from "./FileList";

const BrowseFiles: FunctionComponent<RouteComponentProps> = () => {
  const ALL = "All";
  const [currentType, setCurrentType] = useState<string>(ALL);
  const [types, setTypes] = useState<string[]>([]);
  const [files, setFiles] = useState<File[]>([]);
  const [directory, setDirectory] = useState<string>("");
  const [isLoading, setLoading] = useState(false);

  useEffect(() => {
    fileService.getCurrentDirectory().then(response => {
      setDirectory(response.data);
    });
  }, []);

  useEffect(() => {
    if (currentType === ALL) {
      fileService.getFiles().then(response => {
        setFiles(response.data);
      });
    } else {
      fileService.getFileByExtension(currentType).then(response => {
        setFiles(response.data);
      });
    }
  }, [currentType]);

  useEffect(() => {
    fileService.getExtensions().then(response => {
      setTypes(response.data);
    });
  }, [files]);

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
    setLoading(true)
    fileService.sync(directory).then(response => {
      setFiles(response.data);
      setLoading(false)
    });
  }

  return (
    <div className="vertical-container">
      <div>
        <Button
          icon={isLoading ? <Spinner size={Spinner.SIZE_SMALL}/>:"refresh"}
          text="Synchronize"
          onClick={() => handleSynchronize()}
          disabled={isLoading}
        />
        <label className="editable-label"> Directory: </label>
        <EditableText
          className="editable-text"
          value={directory}
          onChange={e => setDirectory(e)}
        />
      </div>
      <div className="horizontal-container">
        <div className="left-nav-section">
          <ButtonGroup vertical={true}>
            <Button
              intent={currentType === ALL ? Intent.PRIMARY : Intent.NONE}
              onClick={() => handleChangeType(ALL)}
            >
              {ALL}
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
