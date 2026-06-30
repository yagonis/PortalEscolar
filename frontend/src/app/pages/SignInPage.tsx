import { useState } from "react";
import { Button } from "../components/ui/button";
import { Input } from "../components/ui/input";
import { Label } from "../components/ui/label";
import { Card, CardContent, CardHeader, CardTitle } from "../components/ui/card";

type Role = "ADMIN" | "USER";

export function UserCreatePage() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState<Role>("USER");

  async function salvarUsuario() {
    const token = localStorage.getItem("token");

    const resposta = await fetch("http://localhost:8080/api/users", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({
        name,
        email,
        password,
        role,
      }),
    });

    if (!resposta.ok) {
      alert("Erro ao cadastrar usuário");
      return;
    }

    alert("Usuário cadastrado com sucesso");
    setName("");
    setEmail("");
    setPassword("");
    setRole("USER");
  }

  return (
    <div className="max-w-xl mx-auto p-6">
      <Card>
        <CardHeader>
          <CardTitle>Cadastrar Usuário</CardTitle>
        </CardHeader>

        <CardContent className="space-y-4">
          <div className="space-y-2">
            <Label>Nome</Label>
            <Input value={name} onChange={(e) => setName(e.target.value)} />
          </div>

          <div className="space-y-2">
            <Label>Email</Label>
            <Input value={email} onChange={(e) => setEmail(e.target.value)} />
          </div>

          <div className="space-y-2">
            <Label>Senha</Label>
            <Input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>

          <div className="space-y-2">
            <Label>Perfil</Label>
            <select
              className="w-full border rounded-md h-10 px-3"
              value={role}
              onChange={(e) => setRole(e.target.value as Role)}
            >
              <option value="USER">Usuário comum</option>
              <option value="ADMIN">Administrador</option>
            </select>
          </div>

          <Button onClick={salvarUsuario} className="w-full">
            Cadastrar
          </Button>
        </CardContent>
      </Card>
    </div>
  );
}