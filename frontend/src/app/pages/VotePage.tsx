import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import {
  listarEnquetesAbertas,
  votarEnquete,
  type Poll,
} from "../services/polls";
import { Button } from "../components/ui/button";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "../components/ui/card";

export function VotePage() {
  const navigate = useNavigate();

  const [enquetes, setEnquetes] = useState<Poll[]>([]);
  const [selecionadas, setSelecionadas] = useState<
    Record<string, string>
  >({});
  const [mensagens, setMensagens] = useState<
    Record<string, string>
  >({});
  const [carregando, setCarregando] = useState(true);
  const [votando, setVotando] = useState<string | null>(null);

  useEffect(() => {
    carregarEnquetes();
  }, []);

  async function carregarEnquetes() {
    try {
      setCarregando(true);

      const dados = await listarEnquetesAbertas();

      setEnquetes(dados);
    } catch (erro) {
      console.error(erro);
    } finally {
      setCarregando(false);
    }
  }

  function selecionarOpcao(
    pollId: string,
    optionId: string
  ) {
    setSelecionadas((estadoAtual) => ({
      ...estadoAtual,
      [pollId]: optionId,
    }));

    setMensagens((estadoAtual) => ({
      ...estadoAtual,
      [pollId]: "",
    }));
  }

  async function confirmarVoto(pollId: string) {
    const optionId = selecionadas[pollId];

    if (!optionId) {
      setMensagens((estadoAtual) => ({
        ...estadoAtual,
        [pollId]: "Selecione uma opção.",
      }));

      return;
    }

    try {
      setVotando(pollId);

      await votarEnquete(pollId, optionId);

      setMensagens((estadoAtual) => ({
        ...estadoAtual,
        [pollId]: "Voto registrado com sucesso!",
      }));

      setSelecionadas((estadoAtual) => ({
        ...estadoAtual,
        [pollId]: "",
      }));
    } catch (erro) {
      const mensagemErro =
        erro instanceof Error ? erro.message : "";

      if (mensagemErro === "AUTH_REQUIRED") {
        navigate("/login");
        return;
      }

      setMensagens((estadoAtual) => ({
        ...estadoAtual,
        [pollId]:
          mensagemErro ||
          "Não foi possível registrar o voto.",
      }));
    } finally {
      setVotando(null);
    }
  }

  if (carregando) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        Carregando enquetes...
      </div>
    );
  }

  return (
    <main className="min-h-screen bg-muted/30 px-4 py-8">
      <div className="max-w-4xl mx-auto space-y-6">
        <div>
          <h1 className="text-3xl font-bold">
            Enquetes
          </h1>

          <p className="text-muted-foreground">
            Participe das decisões e consultas do portal.
          </p>
        </div>

        {enquetes.length === 0 ? (
          <Card>
            <CardContent className="p-6 text-center text-muted-foreground">
              Nenhuma enquete aberta no momento.
            </CardContent>
          </Card>
        ) : (
          enquetes.map((enquete) => (
            <Card key={enquete.id}>
              <CardHeader>
                <CardTitle>
                  {enquete.question}
                </CardTitle>

                {enquete.description && (
                  <p className="text-muted-foreground">
                    {enquete.description}
                  </p>
                )}
              </CardHeader>

              <CardContent className="space-y-4">
                <div className="space-y-2">
                  {[...enquete.options]
                    .sort(
                      (a, b) =>
                        a.displayOrder - b.displayOrder
                    )
                    .map((opcao) => (
                      <label
                        key={opcao.id}
                        className="flex items-center gap-3 border rounded-lg p-3 cursor-pointer hover:bg-muted"
                      >
                        <input
                          type="radio"
                          name={`poll-${enquete.id}`}
                          checked={
                            selecionadas[enquete.id] ===
                            opcao.id
                          }
                          onChange={() =>
                            selecionarOpcao(
                              enquete.id,
                              opcao.id
                            )
                          }
                        />

                        <span>{opcao.text}</span>
                      </label>
                    ))}
                </div>

                {mensagens[enquete.id] && (
                  <p className="text-sm text-muted-foreground">
                    {mensagens[enquete.id]}
                  </p>
                )}

                <Button
                  onClick={() =>
                    confirmarVoto(enquete.id)
                  }
                  disabled={
                    votando === enquete.id ||
                    !selecionadas[enquete.id]
                  }
                >
                  {votando === enquete.id
                    ? "Registrando..."
                    : "Votar"}
                </Button>
              </CardContent>
            </Card>
          ))
        )}
      </div>
    </main>
  );
}