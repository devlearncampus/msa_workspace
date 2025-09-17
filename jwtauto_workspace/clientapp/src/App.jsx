// src/App.jsx
import { useState } from "react";
import api from "./utils/api";

export default function App() {
  const [username, setUsername] = useState("user");
  const [password, setPassword] = useState("1234");
  const [me, setMe] = useState(null);
  const [msg, setMsg] = useState("");

  const login = async (e) => {
    e.preventDefault();
    setMsg("");
    try {
      const res = await api.post("/api/auth/login", { username, password });
      // 서버가 내려준 accessToken 저장
      localStorage.setItem("accessToken", res.data.accessToken);
      setMsg("로그인 성공");
    } catch (err) {
      setMsg("로그인 실패");
    }
  };

  const loadMe = async () => {
    try {
      const res = await api.get("/api/me");
      setMe(res.data);
      setMsg("me 조회 성공");
    } catch {
      setMsg("me 조회 실패(토큰 필요)");
    }
  };

  const logout = async () => {
    try {
      // 서버 로그아웃(블랙리스트 등록)
      await api.post("/api/auth/logout");
    } catch {
      // 실패하더라도 로컬 토큰 삭제는 진행
    } finally {
      localStorage.removeItem("accessToken");
      setMe(null);
      setMsg("로그아웃 완료");
    }
  };

  return (
    <div style={{ padding: 24 }}>
      <h2>로그인</h2>
      <form onSubmit={login} style={{ marginBottom: 16 }}>
        <input placeholder="username" value={username} onChange={e=>setUsername(e.target.value)} />
        <input placeholder="password" type="password" value={password} onChange={e=>setPassword(e.target.value)} />
        <button type="submit">로그인</button>
      </form>

      <button onClick={loadMe}>/api/me 호출</button>
      <button onClick={logout} style={{ marginLeft: 8 }}>로그아웃</button>

      <div style={{ marginTop: 16, whiteSpace: "pre-wrap" }}>
        <b>메시지:</b> {msg}
      </div>
      <div style={{ marginTop: 16 }}>
        <b>me:</b> {me ? JSON.stringify(me, null, 2) : "(없음)"}
      </div>
    </div>
  );
}
