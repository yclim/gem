import { AxiosResponse } from "axios";
import Api from "./Api";
import { ExtractConfig } from ".";

class ExtractConfigService extends Api {
  public constructor() {
    super();
  }

  public getExtractConfig(
    groupId: number
  ): Promise<AxiosResponse<ExtractConfig>> {
    return this.get<ExtractConfig>("extract/config/" + groupId);
  }
}

export default new ExtractConfigService();
