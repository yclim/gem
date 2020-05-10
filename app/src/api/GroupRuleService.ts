import { AxiosResponse } from "axios";
import Api from "./Api";
import { Group, Rule } from ".";

class GroupRuleService extends Api {
  public constructor() {
    super();
  }

  public getGroups(): Promise<AxiosResponse<Group[]>> {
    return this.get<Group[]>("group/list");
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
