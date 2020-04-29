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

  public getRules(): Promise<AxiosResponse<Rule[]>> {
    return this.get<Rule[]>("rule/list");
  }

  public updateGroupName(oldGroupName: string, newGroupName: string): Promise<AxiosResponse<Rule[]>> {
    return this.put("group/name", '', { params: {
        oldGroupName, newGroupName }});
  }

  public deleteGroup(groupName: string): Promise<AxiosResponse<Rule[]>> {
    return this.delete("group/name?name=" + groupName);
  }

  public saveGroup(group: Group): Promise<AxiosResponse<any>> {
    return this.post<any>("group", JSON.stringify(group), {
      headers: { "Content-Type": "application/json" }
    });
  }
}

export default new GroupRuleService();
