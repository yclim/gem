import React, { useState, useEffect, FunctionComponent } from "react";
import { Intent, Button, Card, Elevation, Tag } from "@blueprintjs/core";
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
    let name = "untitled";
    let counter = 1;
    console.log(groups);
    while (true) {
      let mod_name = name + "-" + counter;
      if (!groups.has(mod_name)) {
        setGroups(
          new Map(groups.set(mod_name, { groupName: mod_name, rules: [] }))
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
    <div className="flex-container-vertical">
      <div className="toolbar">
        <Button
          icon="add"
          large={false}
          text="Create Group"
          onClick={() => createGroup()}
          className="add-right-margin"
        />
        <Button icon="export" text="Export Spec" />
      </div>
      <div className="flex-wrap">
        {Array.from(groups, ([k, v]) => v).map(g => (
          <GroupCard
            key={g.groupName}
            group={g}
            updateGroups={updateGroup}
            focusGrp={currentGroup}
            setFocusGrp={setCurrentGroup}
          ></GroupCard>
        ))}
        <Card
          interactive={false}
          elevation={Elevation.ZERO}
          className="card no-group-card"
        >
          <div className="rule-header">
            <div className="label">No matches</div>
            <div className="counter">
              <Tag round={true} intent={Intent.WARNING}>
                321
              </Tag>
            </div>
          </div>
          <div className="rule-header">
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
