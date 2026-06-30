import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router";
import {
  listarEnquetesAbertas,
  votarEnquete,
  type Poll,
} from "../services/polls";
import { Button } from "./ui/button";
import { Card, CardContent } from "./ui/card";

export function ParticipacaoCidada() {
  const navigate = useNavigate();

  const [enquete, setEnquete] = useState<Poll | null>(null);
  const [opcaoSelecionada, setOpcaoSelecionada] = useState("");
  const [carregando, setCarregando] = useState(true);
  const [votando, setVotando] = useState(false);
  const [mensagem, setMensagem] = useState("");

  useEffect(() => {
    carregarEnquete();
  }, []);

  async function carregarEnquete() {
    try {
      setCarregando(true);
      setMensagem("");

      const enquetes = await listarEnquetesAbertas();

      setEnquete(enquetes[0] ?? null);
    } catch (erro) {
      console.error(erro);
      setMensagem("Não foi possível carregar a enquete.");
    } finally {
      setCarregando(false);
    }
  }

  async function confirmarVoto() {
    if (!enquete) {
      return;
    }

    if (!opcaoSelecionada) {
      setMensagem("Selecione uma opção.");
      return;
    }

    try {
      setVotando(true);
      setMensagem("");

      await votarEnquete(enquete.id, opcaoSelecionada);

      setMensagem("Voto registrado com sucesso!");
      setOpcaoSelecionada("");
    } catch (erro) {
      const mensagemErro =
        erro instanceof Error ? erro.message : "";

      if (mensagemErro === "AUTH_REQUIRED") {
        navigate("/login");
        return;
      }

      setMensagem(
        mensagemErro || "Não foi possível registrar o voto."
      );
    } finally {
      setVotando(false);
    }
  }

  if (carregando) {
    return (
      <Card>
        <CardContent className="p-6">
          Carregando enquete...
        </CardContent>
      </Card>
    );
  }

  if (!enquete) {
    return (
      <Card>
        <CardContent className="p-6 space-y-4">
          <p className="text-muted-foreground">
            Nenhuma enquete disponível no momento.
          </p>

          <Button variant="outline" asChild>
            <Link to="/vote">Ver todas as enquetes</Link>
          </Button>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardContent className="p-6 space-y-5">
        <div>
          <h2 className="text-xl font-semibold">
            Participação cidadã
          </h2>

          <p className="text-muted-foreground mt-1">
            {enquete.question}
          </p>

          {enquete.description && (
            <p className="text-sm text-muted-foreground mt-2">
              {enquete.description}
            </p>
          )}
        </div>

        <div className="space-y-2">
          {[...enquete.options]
            .sort((a, b) => a.displayOrder - b.displayOrder)
            .map((opcao) => (
              <label
                key={opcao.id}
                className="flex items-center gap-3 border rounded-lg p-3 cursor-pointer hover:bg-muted"
              >
                <input
                  type="radio"
                  name={`home-poll-${enquete.id}`}
                  value={opcao.id}
                  checked={opcaoSelecionada === opcao.id}
                  onChange={() =>
                    setOpcaoSelecionada(opcao.id)
                  }
                />

                <span>{opcao.text}</span>
              </label>
            ))}
        </div>

        {mensagem && (
          <p className="text-sm text-muted-foreground">
            {mensagem}
          </p>
        )}

        <div className="flex gap-2">
          <Button
            onClick={confirmarVoto}
            disabled={votando || !opcaoSelecionada}
          >
            {votando ? "Registrando..." : "Votar"}
          </Button>

          <Button variant="outline" asChild>
            <Link to="/vote">Ver todas</Link>
          </Button>
        </div>
      </CardContent>
    </Card>
  );
}