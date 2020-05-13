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

  public saveExtractConfig(
    config: ExtractConfig
  ): Promise<AxiosResponse<ExtractConfig>> {
    const jsonStr = JSON.stringify(config);
    return this.put<ExtractConfig>(
      "extract/config/" + config.groupId,
      jsonStr,
      {
        headers: { "Content-Type": "application/json" }
      }
    );
  }
}

export default new ExtractConfigService();
