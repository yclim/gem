import axios, { AxiosRequestConfig, AxiosResponse } from "axios";
import Api from "./api";
import { File } from ".";

class FileService extends Api {
  public constructor() {
    super();
    this.sync = this.sync.bind(this);
    this.getFiles = this.getFiles.bind(this);
    this.getFileByExtension = this.getFileByExtension.bind(this);
    this.getCurrentDirectory = this.getCurrentDirectory.bind(this);
    this.getExtensions = this.getExtensions.bind(this);
  }

  public sync(directory: string): Promise<AxiosResponse<File[]>> {
    return this.get<File[]>("file/sync", { params: { directory } });
  }

  public getFiles(): Promise<AxiosResponse<File[]>> {
    return this.get<File[]>("file/list");
  }

  public getFileByExtension(extension: string): Promise<AxiosResponse<File[]>> {
    return this.get<File[]>("file/findByExtension/" + extension);
  }

  public getCurrentDirectory(): Promise<AxiosResponse<string>> {
    return this.get<string>("file/currentDir");
  }

  public getExtensions(): Promise<AxiosResponse<string[]>> {
    return this.get<string[]>("file/extensions/list");
  }
}

export default new FileService();
