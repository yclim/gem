import React, { FunctionComponent, useContext, useEffect } from "react";
import {
  AnchorButton,
  Button,
  Card,
  Elevation,
  FileInput,
  Intent,
  Tag
} from "@blueprintjs/core";
import { Group } from "./api";
import groupRuleService from "./api/GroupRuleService";
import GroupCard from "./GroupCard";
import { StoreContext } from "./StoreContext";

interface IProps {
  currentGroup: Group | null;
  setCurrentGroup: (group: Group) => void;
  newGroupRuleName: string | null;
  setFiles: (fs: File[]) => void;
}

const GroupList: FunctionComponent<IProps> = ({
  currentGroup,
  setCurrentGroup,
  newGroupRuleName,
  setFiles
}) => {
  const context = useContext(StoreContext);

  function handleCreateGroup() {
    context.groupsAction?.newGroup();
  }

  function handleFileSelected(file: string) {
    const selectedFile = file;
    const data = new FormData();
    data.append("file", selectedFile);
    groupRuleService.importGroupsFile(data).then(results => {
    });
  }

  function handleNoMatchGroupSelected() {
    setCurrentGroup(null);
    setFiles(context.fileStatState.noMatch);
  }
  function handleConflictGroupSelected() {
    setCurrentGroup(null);
    setFiles(context.fileStatState.conflict);
  }

  useEffect(() => {
    context.fileStatAction?.initFileStat();
    if (currentGroup != null) {
      groupRuleService.getGroup(currentGroup.name).then(response => {
        // Check for undefined to takecare of deleted group
        if (typeof response.data.matchedFile !== "undefined") {
          setFiles(response.data.matchedFile);
        } else {
          setFiles([]);
        }
      });
    }
  }, [currentGroup, context.groupsState]);


  return (
    <div className="stack">
      <div>
        <Button
          icon="add"
          large={false}
          text="Create Group"
          onClick={() => handleCreateGroup()}
          className="add-right-margin"
        />
        <AnchorButton
          icon="export"
          text="Export Spec"
          href="/api/group/export"
        />
      </div>
      <div>
        <FileInput
          text="Choose spec file..."
          buttonText="Import"
          onInputChange={(e: React.ChangeEvent<HTMLInputElement>) =>
            handleFileSelected(e.target.value)
          }
        />
      </div>
      <div className="stack">
        {Array.from(context.groupsState, ([k, v]) => v)
          .sort((n1, n2) => {
            if (n1.name > n2.name) {
              return 1;
            }
            if (n1.name < n2.name) {
              return -1;
            }
            return 0;
          })
          .map(g => (
            <GroupCard
              key={g.name}
              group={g}
              focusGroup={currentGroup}
              setFocusGroup={setCurrentGroup}
              newGroupRuleName={newGroupRuleName}
            />
          ))}
        <div>
        { typeof context.fileStatState.noMatch !== "undefined" && context.fileStatState.noMatch.length>0  ?
            <Card interactive={true}
              elevation={Elevation.ZERO}
              onClick={handleNoMatchGroupSelected}
              className="no-group-card "
            >
              <div className="group-card-header">
                <div className="label">No matches</div>
                <div className="counter">
                  <Tag round={true} intent={Intent.WARNING}>
                    {context.fileStatState.noMatch.length}
                  </Tag>
                </div>
              </div>
            </Card>: null
           }
        </div>
        <div>
        { typeof context.fileStatState.conflict !== "undefined" && context.fileStatState.conflict.length>0  ?
        <Card
          interactive={true}
          elevation={Elevation.ZERO}
          onClick={handleConflictGroupSelected}
          className="conflict-group-card"
        >
          <div className="group-card-header">
            <div className="label">Conflicts</div>
            <div className="counter">
              <Tag round={true} intent={Intent.DANGER}>
                {context.fileStatState.conflict.length}
              </Tag>
            </div>
          </div>
        </Card>: null
           }
        </div>
      </div>
    </div>
  );
};

export default GroupList;
