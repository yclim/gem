import axios, { AxiosRequestConfig, AxiosResponse } from "axios";
import Api from "./Api";
import { File, Group, Rule } from ".";

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

  public saveGroup(group: Group): Promise<AxiosResponse<any>> {
    return this.post<any>("group", JSON.stringify(group), {
      headers: { "Content-Type": "application/json" }
    });
  }
}

export default new GroupRuleService();
