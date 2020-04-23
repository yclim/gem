import React, { FunctionComponent } from "react";
import { Button, Card, Elevation, Intent, Tag } from "@blueprintjs/core";
import { Group } from "./api";
import GroupCard from "./GroupCard";

interface IProps {
  groups: Map<string, Group>;
  setGroups: (group: Map<string, Group>) => void;
  currentGroup: Group | null;
  setCurrentGroup: (group: Group) => void;
}

const GroupList: FunctionComponent<IProps> = ({
  groups,
  setGroups,
  currentGroup,
  setCurrentGroup
}) => {
  function createGroup() {
    const name = "untitled";
    let counter = 1;
    console.log(groups);
    while (true) {
      const modName = name + "-" + counter;
      if (!groups.has(modName)) {
        setGroups(
          new Map(groups.set(modName, { groupName: modName, rules: [] }))
        );
        return;
      } else {
        counter++;
      }
    }
  }

  function updateGroup(oldname: string, group: Group) {
    groups.delete(oldname);
    setGroups(
      new Map([...groups.set(group.groupName, group).entries()].sort())
    );
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
            key={g.groupName}
            group={g}
            updateGroups={updateGroup}
            focusGrp={currentGroup}
            setFocusGrp={setCurrentGroup}
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
