import { useEffect, useState } from "react";
import { Newspaper, ArrowRight } from "lucide-react";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "./ui/card";
import { Button } from "./ui/button";

type Noticia = {
  id: string;
  title: string;
  subtitle: string | null;
  body: string;
  imageUrl: string | null;
  status: string;
  publishedAt: string | null;
  updatedAt: string | null;
};

export function UltimasNoticias() {
  const [noticias, setNoticias] = useState<Noticia[]>([]);

  useEffect(() => {
    async function carregarNoticias() {
      try {
        const resposta = await fetch("http://localhost:8080/api/news");
        const dados = await resposta.json();

        setNoticias(dados.content ?? []);
      } catch (erro) {
        console.error("Erro ao carregar notícias:", erro);
      }
    }

    carregarNoticias();
  }, []);

  return (
    <Card>
      <CardHeader>
        <div className="flex items-center justify-between">
          <div>
            <CardTitle className="flex items-center gap-2">
              <Newspaper className="size-5 text-primary" />
              Últimas Notícias
            </CardTitle>
            <CardDescription>Fique por dentro das novidades da escola</CardDescription>
          </div>
          <Button variant="outline" size="sm">Ver Todas</Button>
        </div>
      </CardHeader>

      <CardContent>
        <div className="grid md:grid-cols-3 gap-4">
          {noticias.map((noticia) => (
            <div
              key={noticia.id}
              className="group border rounded-lg overflow-hidden hover:shadow-lg transition-all cursor-pointer"
            >
              <div className={`h-40 ${noticia.imageUrl ?? "bg-gradient-to-br from-blue-400 to-indigo-600"}`} />

              <div className="p-4">
                <p className="text-xs text-muted-foreground mb-2">
                  {noticia.publishedAt ?? "Não publicada"}
                </p>

                <h3 className="font-medium mb-2 group-hover:text-primary transition-colors">
                  {noticia.title}
                </h3>

                <p className="text-sm text-muted-foreground mb-3">
                  {noticia.subtitle}
                </p>

                <Button variant="ghost" size="sm" className="p-0 h-auto">
                  Ler mais <ArrowRight className="size-4 ml-1" />
                </Button>
              </div>
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  );
}