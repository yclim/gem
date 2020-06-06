import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from "axios";

export default class Api {
  private api: AxiosInstance;

  public constructor() {
    this.api = axios.create({
      baseURL: "api"
      //  other settings here
    });

    this.api.interceptors.request.use(
      (requestConfig: AxiosRequestConfig) => {
        return { ...requestConfig };
      },
      error => {
        alert(error);
        return Promise.reject(error);
      }
    );

    this.api.interceptors.response.use(
      (response: AxiosResponse) => {
        return {
          ...response
        };
      },
      error => {
        alert(error);
        return Promise.reject(error);
      }
    );
  }

  public request<T, R = AxiosResponse<T>>(
    config: AxiosRequestConfig
  ): Promise<R> {
    return this.api.request(config);
  }

  public get<T, R = AxiosResponse<T>>(
    url: string,
    config?: AxiosRequestConfig
  ): Promise<R> {
    return this.api.get(url, config);
  }

  public delete<T, R = AxiosResponse<T>>(
    url: string,
    config?: AxiosRequestConfig
  ): Promise<R> {
    return this.api.delete(url, config);
  }

  public head<T, R = AxiosResponse<T>>(
    url: string,
    config?: AxiosRequestConfig
  ): Promise<R> {
    return this.api.head(url, config);
  }

  public post<T, R = AxiosResponse<T>>(
    url: string,
    data?: any,
    config?: AxiosRequestConfig
  ): Promise<R> {
    return this.api.post(url, data, config);
  }

  public put<T, R = AxiosResponse<T>>(
    url: string,
    data?: any,
    config?: AxiosRequestConfig
  ): Promise<R> {
    return this.api.put(url, data, config);
  }

  public patch<T, R = AxiosResponse<T>>(
    url: string,
    data?: string,
    config?: AxiosRequestConfig
  ): Promise<R> {
    return this.api.patch(url, data, config);
  }
}
