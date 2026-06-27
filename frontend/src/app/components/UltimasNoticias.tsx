import { Newspaper, ArrowRight } from "lucide-react";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "./ui/card";
import { Button } from "./ui/button";

export function UltimasNoticias() {
  const noticias = [
    {
      id: 1,
      titulo: "Alunos do 5º ano conquistam medalhas na Olimpíada de Matemática",
      resumo: "Nossa escola teve destaque na competição regional com 8 medalhas conquistadas pelos estudantes.",
      data: "25/05/2026",
      imagem: "bg-gradient-to-br from-yellow-400 to-orange-500"
    },
    {
      id: 2,
      titulo: "Projeto de Sustentabilidade é apresentado na comunidade",
      resumo: "Iniciativa dos alunos do ensino médio sobre reciclagem e preservação ambiental.",
      data: "23/05/2026",
      imagem: "bg-gradient-to-br from-green-400 to-emerald-600"
    },
    {
      id: 3,
      titulo: "Nova biblioteca digital disponível para todos os alunos",
      resumo: "Acervo digital com mais de 5 mil títulos já pode ser acessado pelo portal.",
      data: "20/05/2026",
      imagem: "bg-gradient-to-br from-blue-400 to-indigo-600"
    }
  ];

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
              <div className={`h-40 ${noticia.imagem}`} />
              <div className="p-4">
                <p className="text-xs text-muted-foreground mb-2">{noticia.data}</p>
                <h3 className="font-medium mb-2 group-hover:text-primary transition-colors">
                  {noticia.titulo}
                </h3>
                <p className="text-sm text-muted-foreground mb-3">{noticia.resumo}</p>
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
