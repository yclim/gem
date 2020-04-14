import axios from "axios";

const API_DOMAIN = "http://localhost:8080";
const END_POINT = "/api/file";

class GEMFileAPIService {
  sync(directory) {
    const url = API_DOMAIN + END_POINT + "/sync";
    const params = { directory: directory };
    return axios.get(url, { params: params });
  }
  getFiles() {
    const url = API_DOMAIN + END_POINT + "/list";
    return axios.get(url);
  }
  getFilesByType(type) {
    const url = API_DOMAIN + END_POINT + "/findByExtension/" + type;
    return axios.get(url);
  }
  getCurrentDirectory() {
    const url = API_DOMAIN + END_POINT + "/currentDir";
    return axios.get(url);
  }
  getFileTypes() {
    const url = API_DOMAIN + END_POINT + "/extensions/list";
    return axios.get(url);
  }
}

export default new GEMFileAPIService();
