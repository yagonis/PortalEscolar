import { useEffect, useState } from "react";
import { listarNoticiasPublicas } from "../services/news";
import { Card, CardContent } from "../components/ui/card";
import { Button } from "../components/ui/button";

type Noticia = {
  id: string;
  title: string;
  subtitle?: string;
  body: string;
  imageUrl?: string;
  status: "PUBLISHED" | "DRAFT" | "ARCHIVED";
};

export function NewsPage() {
  const [noticias, setNoticias] = useState<Noticia[]>([]);
  const [noticiaSelecionada, setNoticiaSelecionada] = useState<Noticia | null>(null);
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    carregarNoticias();
  }, []);

  async function carregarNoticias() {
    try {
      setCarregando(true);

      const dados = await listarNoticiasPublicas();

      setNoticias(dados.content ?? []);
    } catch (erro) {
      console.error(erro);
      alert("Erro ao carregar notícias.");
    } finally {
      setCarregando(false);
    }
  }

  if (carregando) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p>Carregando notícias...</p>
      </div>
    );
  }

  if (noticiaSelecionada) {
    return (
      <div className="min-h-screen bg-muted/30 px-4 py-8">
        <div className="max-w-3xl mx-auto">
          <Button
            variant="outline"
            onClick={() => setNoticiaSelecionada(null)}
            className="mb-4"
          >
            Voltar
          </Button>

          <Card>
            {noticiaSelecionada.imageUrl && (
              <img
                src={noticiaSelecionada.imageUrl}
                alt={noticiaSelecionada.title}
                className="w-full h-72 object-cover rounded-t-xl"
              />
            )}

            <CardContent className="p-6 space-y-4">
              <h1 className="text-3xl font-bold">
                {noticiaSelecionada.title}
              </h1>

              {noticiaSelecionada.subtitle && (
                <p className="text-muted-foreground text-lg">
                  {noticiaSelecionada.subtitle}
                </p>
              )}

              <p className="whitespace-pre-line leading-7">
                {noticiaSelecionada.body}
              </p>
            </CardContent>
          </Card>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-muted/30 px-4 py-8">
      <div className="max-w-5xl mx-auto space-y-6">
        <div>
          <h1 className="text-3xl font-bold">Notícias</h1>
          <p className="text-muted-foreground">
            Acompanhe as últimas informações publicadas no portal.
          </p>
        </div>

        {noticias.length === 0 ? (
          <Card>
            <CardContent className="p-6 text-center text-muted-foreground">
              Nenhuma notícia publicada no momento.
            </CardContent>
          </Card>
        ) : (
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            {noticias.map((noticia) => (
              <Card
                key={noticia.id}
                className="overflow-hidden cursor-pointer hover:shadow-md transition-shadow"
                onClick={() => setNoticiaSelecionada(noticia)}
              >
                {noticia.imageUrl && (
                  <img
                    src={noticia.imageUrl}
                    alt={noticia.title}
                    className="w-full h-40 object-cover"
                  />
                )}

                <CardContent className="p-4 space-y-2">
                  <h2 className="font-semibold text-lg line-clamp-2">
                    {noticia.title}
                  </h2>

                  {noticia.subtitle && (
                    <p className="text-sm text-muted-foreground line-clamp-3">
                      {noticia.subtitle}
                    </p>
                  )}

                  <Button variant="link" className="px-0">
                    Ler notícia
                  </Button>
                </CardContent>
              </Card>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}