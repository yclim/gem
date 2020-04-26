import React, { Dispatch, FunctionComponent } from "react";
import { Button, Card, Elevation, Intent, Tag } from "@blueprintjs/core";
import { Group } from "./api";
import GroupCard from "./GroupCard";
import { GroupAction, GroupActions } from "./EditGroups";

interface IProps {
  groups: Map<string, Group>;
  groupDispatcher: Dispatch<GroupAction>;
  currentGroup: Group | null;
  setCurrentGroup: (group: Group) => void;
  focusedGroupRuleName: string | null;
}

const GroupList: FunctionComponent<IProps> = ({
  groups,
  groupDispatcher,
  currentGroup,
  setCurrentGroup,
  focusedGroupRuleName
}) => {
  function createGroup() {
    groupDispatcher(GroupActions.newGroupAction());
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
      <div className="stack">
        {Array.from(groups, ([k, v]) => v).map(g => (
          <GroupCard
            key={g.name}
            group={g}
            groupDispatcher={groupDispatcher}
            focusGrp={currentGroup}
            setFocusGrp={setCurrentGroup}
            focusedGroupRuleName={focusedGroupRuleName}
          />
        ))}
        <Card interactive={false} elevation={Elevation.ZERO}>
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
