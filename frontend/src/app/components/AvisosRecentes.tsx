import { AlertCircle, Pin, MessageSquare } from "lucide-react";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "./ui/card";
import { Badge } from "./ui/badge";
import { Button } from "./ui/button";

export function AvisosRecentes() {
  const avisos = [
    {
      id: 1,
      titulo: "Reunião de Pais - 1º Bimestre",
      descricao: "Convocamos todos os responsáveis para a reunião de apresentação dos resultados do primeiro bimestre. Compareçam!",
      data: "26/05/2026",
      categoria: "Reunião",
      fixado: true,
      comentarios: 12
    },
    {
      id: 2,
      titulo: "Horário de Funcionamento - Feriado",
      descricao: "A escola estará fechada no dia 07/06/2026 devido ao feriado nacional. Retornaremos normalmente no dia 08/06/2026.",
      data: "25/05/2026",
      categoria: "Informativo",
      fixado: false,
      comentarios: 5
    },
    {
      id: 3,
      titulo: "Entrega de Uniformes",
      descricao: "Os uniformes escolares já estão disponíveis para retirada na secretaria. Horário: 8h às 17h.",
      data: "24/05/2026",
      categoria: "Uniforme",
      fixado: false,
      comentarios: 23
    },
    {
      id: 4,
      titulo: "Atualização do Calendário Escolar",
      descricao: "O calendário de provas do 2º bimestre foi atualizado. Confira as novas datas no portal.",
      data: "23/05/2026",
      categoria: "Calendário",
      fixado: false,
      comentarios: 8
    }
  ];

  return (
    <Card>
      <CardHeader>
        <div className="flex items-center justify-between">
          <div>
            <CardTitle className="flex items-center gap-2">
              <AlertCircle className="size-5 text-primary" />
              Avisos Recentes
            </CardTitle>
            <CardDescription>Comunicados importantes da escola</CardDescription>
          </div>
          <Button variant="outline" size="sm">Ver Todos</Button>
        </div>
      </CardHeader>
      <CardContent>
        <div className="space-y-4">
          {avisos.map((aviso) => (
            <div
              key={aviso.id}
              className="p-4 border rounded-lg hover:bg-accent/50 transition-colors cursor-pointer"
            >
              <div className="flex items-start justify-between gap-4">
                <div className="flex-1">
                  <div className="flex items-center gap-2 mb-2">
                    {aviso.fixado && (
                      <Pin className="size-4 text-primary fill-primary" />
                    )}
                    <h3 className="font-medium">{aviso.titulo}</h3>
                  </div>
                  <p className="text-sm text-muted-foreground mb-3">{aviso.descricao}</p>
                  <div className="flex items-center gap-3 text-xs text-muted-foreground">
                    <span>{aviso.data}</span>
                    <Badge variant="secondary" className="text-xs">{aviso.categoria}</Badge>
                    <span className="flex items-center gap-1">
                      <MessageSquare className="size-3" />
                      {aviso.comentarios}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  );
}
