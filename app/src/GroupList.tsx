import React, { FunctionComponent, useContext } from "react";
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
}

const GroupList: FunctionComponent<IProps> = ({
  currentGroup,
  setCurrentGroup,
  newGroupRuleName
}) => {
  const context = useContext(StoreContext);

  function handleCreateGroup() {
    context.groupsAction?.newGroup();
    context.fileStatAction?.initFileStat();
  }

  function handleFileSelected(file: string) {
    const selectedFile = file;
    const data = new FormData();
    data.append("file", selectedFile);
    groupRuleService.importGroupsFile(data).then(results => {
      context.fileStatAction?.initFileStat();
    });
  }

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
        <Card
          interactive={false}
          elevation={Elevation.ZERO}
          className="no-group-card "
        >
          <div className="group-card-header">
            <div className="label">No matches</div>
            <div className="counter">
              <Tag round={true} intent={Intent.WARNING}>
                {context.fileStatState[0]}
              </Tag>
            </div>
          </div>
          <div className="group-card-header">
            <div className="label">Conflicts</div>
            <div className="counter">
              <Tag round={true} intent={Intent.DANGER}>
                {context.fileStatState[1]}
              </Tag>
            </div>
          </div>
        </Card>
      </div>
    </div>
  );
};

export default GroupList;
