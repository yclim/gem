import React, { Dispatch, FunctionComponent } from "react";
import { Button, Card, Elevation, FileInput, Intent, Tag } from "@blueprintjs/core";
import { Group } from "./api";
import groupRuleService from "./api/GroupRuleService";
import GroupCard from "./GroupCard";
import { GroupAction, GroupActions } from "./EditGroups";

interface IProps {
  groups: Map<string, Group>;
  groupDispatcher: Dispatch<GroupAction>;
  currentGroup: Group | null;
  setCurrentGroup: (group: Group) => void;
  newGroupRuleName: string | null;
}

const GroupList: FunctionComponent<IProps> = ({
  groups,
  groupDispatcher,
  currentGroup,
  setCurrentGroup,
  newGroupRuleName
}) => {
  function createGroup() {
    groupDispatcher(GroupActions.newGroupAction());
  }

  function handleFileSelected(evt) {
    const selectedFile = evt.target.files[0];
    const data = new FormData();
    data.append('file', selectedFile);
    groupRuleService.importGroupsFile(data).then(results => {
      if(results.status===200) {
        groupDispatcher(GroupActions.initGroup(results.data));
      } else {
        alert('Import failed');
      }
    });
  }

  return (
    <div className="stack">
      <div>
        <Button
          icon="add"
          large={false}
          text="Create Group"
          onClick={() => createGroup()}
          className="add-right-margin"
        />
        <Button icon="export" text="Export Spec" />
      </div>
      <div>
        <FileInput text="Choose spec file..." buttonText="Import" onInputChange={(evt) => handleFileSelected(evt)} />
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
                321
              </Tag>
            </div>
          </div>
          <div className="group-card-header">
            <div className="label">Conflicts</div>
            <div className="counter">
              <Tag round={true} intent={Intent.DANGER}>
                321
              </Tag>
            </div>
          </div>
        </Card>
      </div>
    </div>
  );
};

export default GroupList;
