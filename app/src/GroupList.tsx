import React, { Dispatch, FunctionComponent } from "react";
import {
  Button,
  AnchorButton,
  Card,
  Elevation,
  FileInput,
  Intent,
  Tag
} from "@blueprintjs/core";
import { Group } from "./api";
import groupRuleService from "./api/GroupRuleService";
import GroupCard from "./GroupCard";
import { GroupAction, GroupActions } from "./EditGroups";
import { FileStatAction, FileStatActions } from "./FileStat";

interface IProps {
  groups: Map<string, Group>;
  groupDispatcher: Dispatch<GroupAction>;
  currentGroup: Group | null;
  setCurrentGroup: (group: Group) => void;
  newGroupRuleName: string | null;
  fileStatDispatcher: Dispatch<FileStatAction>;
  fileStat: number[];
}

const GroupList: FunctionComponent<IProps> = ({
  groups,
  groupDispatcher,
  currentGroup,
  setCurrentGroup,
  newGroupRuleName,
  fileStatDispatcher,
  fileStat
}) => {
  function handleCreateGroup() {
    GroupActions.newGroup(groupDispatcher, groups);
    FileStatActions.getFileStat(fileStatDispatcher);
  }

  function handleFileSelected(file: string) {
    const selectedFile = file;
    const data = new FormData();
    data.append("file", selectedFile);
    groupRuleService.importGroupsFile(data).then(results => {
      GroupActions.initGroup(results.data);
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
        {Array.from(groups, ([k, v]) => v)
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
              groupDispatcher={groupDispatcher}
              focusGroup={currentGroup}
              setFocusGroup={setCurrentGroup}
              newGroupRuleName={newGroupRuleName}
              groups={groups}
              fileStatDispatcher={fileStatDispatcher}
              fileStat={fileStat}
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
                {fileStat[0]}
              </Tag>
            </div>
          </div>
          <div className="group-card-header">
            <div className="label">Conflicts</div>
            <div className="counter">
              <Tag round={true} intent={Intent.DANGER}>
                {fileStat[1]}
              </Tag>
            </div>
          </div>
        </Card>
      </div>
    </div>
  );
};

export default GroupList;
