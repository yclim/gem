import React, { FunctionComponent, useEffect, useState } from "react";
import RuleList from "./RuleList";
import GroupList from "./GroupList";
import { RouteComponentProps } from "@reach/router";
import { File, Group } from "./api";
import ruleService from "./api/mock";
import groupService from "./api/GroupRuleService";
import "@blueprintjs/table/lib/css/table.css";
import FileList from "./FileList";
import { AxiosResponse } from "axios";

const EditGroups: FunctionComponent<RouteComponentProps> = () => {
  const [groups, setGroups] = useState(new Map<string, Group>());
  const [currentGroup, setCurrentGroup] = useState<Group | null>(null);
  const [files, setFiles] = useState<File[]>([]);

  useEffect(() => {
    if (groups.size === 0) {
      groupService.getGroups().then((resp: AxiosResponse<Group[]>) => {
        setGroups(new Map(resp.data.map(g => [g.name, g])));
      });
    }
  }, []);

  useEffect(() => {
    if (currentGroup)
      ruleService.getGroupFiles(currentGroup.name).then(gfs => {
        setFiles(gfs.files);
      });
  }, [currentGroup]);

  return (
    <div className="grid3">
      <RuleList groups={groups} setGroups={setGroups} />
      <GroupList
        groups={groups}
        setGroups={setGroups}
        currentGroup={currentGroup}
        setCurrentGroup={setCurrentGroup}
      />
      <FileList files={files} setFiles={setFiles} />
    </div>
  );
};

export default EditGroups;
