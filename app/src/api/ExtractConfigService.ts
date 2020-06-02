import { AxiosResponse } from "axios";
import Api from "./Api";
import { ExtractConfig, ExtractedData, Extractor, FileCount } from ".";

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

  public getExtractorTemplates(): Promise<AxiosResponse<Extractor[]>> {
    return this.get("extract/extractorTemplates");
  }

  public getFileCounts(groupId: number): Promise<AxiosResponse<FileCount[]>> {
    return this.get("extract/" + groupId);
  }

  public extract(groupId: number): Promise<AxiosResponse<FileCount[]>> {
    return this.post("extract/" + groupId);
  }

  public getExtractedRecords(
    groupId: number,
    absolutePath: string
  ): Promise<AxiosResponse<ExtractedData>> {
    const form = new FormData();
    return this.post(
      "extract/?groupId=" +
        groupId +
        "&absolutePath=" +
        encodeURIComponent(absolutePath)
    );
  }
}

export default new ExtractConfigService();
