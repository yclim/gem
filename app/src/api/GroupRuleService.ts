import { AxiosResponse } from "axios";
import Api from "./Api";
import { ProjectSpec, Group, Rule, FileGroupStat } from ".";

class GroupRuleService extends Api {
  public constructor() {
    super();
  }

  public getGroups(): Promise<AxiosResponse<Group[]>> {
    return this.get<Group[]>("group/list");
  }

  public getFileStat(): Promise<AxiosResponse<number[]>> {
    return this.get<number[]>("group/getFileStat");
  }

  public getFileStatMatches(): Promise<AxiosResponse<FileGroupStat>> {
    return this.get<FileGroupStat>("file/noMatches");
  }

  public getGroup(name: string): Promise<AxiosResponse<Group>> {
    return this.get<Group>("group/name", {
      params: {
        name
      }
    });
  }

  public getRule(ruleId: string): Promise<AxiosResponse<Rule>> {
    return this.get<Rule>("rule", {
      params: {
        ruleId
      }
    });
  }

  public getRules(): Promise<AxiosResponse<Rule[]>> {
    return this.get<Rule[]>("rule/list");
  }

  public updateGroupName(
    oldGroupName: string,
    newGroupName: string
  ): Promise<AxiosResponse<Rule[]>> {
    return this.put("group/name", "", {
      params: {
        oldGroupName,
        newGroupName
      }
    });
  }

  public deleteGroup(groupName: string): Promise<AxiosResponse<Rule[]>> {
    return this.delete("group/name?name=" + groupName);
  }

  public saveGroup(group: Group): Promise<AxiosResponse<Group>> {
    const jsonStr = JSON.stringify(group);
    return this.post<Group>("group", jsonStr, {
      headers: { "Content-Type": "application/json" }
    });
  }

  public importGroupsFile(data: FormData): Promise<AxiosResponse<any>> {
    return this.post<any>("group/import", JSON.stringify(data));
  }
}

export default new GroupRuleService();
