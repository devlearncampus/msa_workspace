// src/api.js
import axios from "axios";

// 서버 베이스 URL (프록시를 쓰지 않는다면 localhost:8080 지정)
const api = axios.create({
  baseURL: "http://localhost:9999",
});

// 토큰 자동 첨부 인터셉터
api.interceptors.request.use(config => {
  const t = localStorage.getItem("accessToken");
  
  if (t) config.headers.Authorization = `Bearer ${t}`;
  return config;
});

export default api;
