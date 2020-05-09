import React, { FunctionComponent, useEffect, useState } from "react";
import { RouteComponentProps } from "@reach/router";
import { Button, ButtonGroup, EditableText, Spinner } from "@blueprintjs/core";
import { Intent } from "@blueprintjs/core/lib/esm/common/intent";
import { File } from "./api";
import fileService from "./api/FileService";
import FileList from "./FileList";
import LoadingBar from 'react-top-loading-bar';


const BrowseFiles: FunctionComponent<RouteComponentProps> = () => {
  const ALL = "All";
  const [currentType, setCurrentType] = useState<string>(ALL);
  const [types, setTypes] = useState<string[]>([]);
  const [files, setFiles] = useState<File[]>([]);
  const [directory, setDirectory] = useState<string>("");
  const [syncStatus, setSyncStatus] = useState(0.0);

  useEffect(() => {
    fileService.getCurrentDirectory().then(response => {
      setDirectory(response.data);
    });
    fileService.getSyncStatus().then(response => {
                    setSyncStatus(response.data);
    });
    const interval = setInterval(() => {
        fileService.getSyncStatus().then(response => {
                setSyncStatus(response.data);
        });
    }, 5000);
    return () => clearInterval(interval);
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
    setSyncStatus(0);
    fileService.sync(directory).then(response => {
      setFiles(response.data);
    });
  }

  return (
    <div className="stack">
      <div>
        <LoadingBar
          progress={ (syncStatus * 100) % 100 }
          height={3}
          color='red'
          onLoaderFinished={() => this.onLoaderFinished()}
        />
        <label className="editable-label"> Directory: </label>
        <EditableText
          className="editable-text"
          value={directory}
          placeholder="/usr/share/gem/files"
          onChange={e => setDirectory(e)}
        />
        <Button
          icon={(syncStatus < 1) ? <Spinner size={Spinner.SIZE_SMALL} /> : "refresh"}
          text="Synchronize"
          onClick={() => handleSynchronize()}
          disabled={(syncStatus < 1)}
        />
      </div>
      <div className="grid2">
        <div className="box">
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
        <div>
          <FileList files={files} setFiles={setFiles} />
        </div>
      </div>
    </div>
  );
};

export default BrowseFiles;
