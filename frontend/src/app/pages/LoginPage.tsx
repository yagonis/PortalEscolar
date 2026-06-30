import { useState } from "react";
import { useNavigate } from "react-router";
import { login } from "../services/auth";
import { Button } from "../components/ui/button";
import { Input } from "../components/ui/input";
import { Label } from "../components/ui/label";


export function LoginPage() {
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  async function handleLogin() {
    try {
      await login(email, password);
      navigate("/admin");
    } catch (error) {
      alert("Email ou senha inválidos");
    }
  }

  async function handleSignIn() {
    try{
      await navigate("/signIn");
    } catch (error) {
      alert("Erro ao navegar para a página de cadastro");
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-muted/30 px-4">
      <div className="w-full max-w-sm bg-card border rounded-xl p-6 space-y-4">
        <h1 className="text-2xl font-semibold text-center">Entrar</h1>

        <div className="space-y-2">
          <Label htmlFor="email">E-mail</Label>
          <Input
            id="email"
            type="email"
            placeholder="seu@email.com"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="password">Senha</Label>
          <Input
            id="password"
            type="password"
            placeholder="Sua senha"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </div>

        <Button onClick={handleLogin} className="w-full">
          Entrar
        </Button>

        <Button onClick={handleSignIn} className="w-full">
          Cadastrar no portal
        </Button>
      </div>
    </div>
  );
}