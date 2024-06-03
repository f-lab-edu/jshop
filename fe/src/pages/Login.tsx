import { useState } from "react"

export default function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  function signIn() {
    
  }
  return (
    <div>
      <input type="username" value={username} onChange={e => setUsername(e.target.value)}/>
      <input type="password" value={password} onChange={e => setPassword(e.target.value)}/>
      <button>sign in</button>
      <button>sign up</button>
    </div>
  )
}