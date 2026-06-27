import { Calendar, MapPin, Clock } from "lucide-react";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "./ui/card";
import { Button } from "./ui/button";

export function ProximosEventos() {
  const eventos = [
    {
      id: 1,
      titulo: "Reunião de Pais",
      data: "30/05/2026",
      hora: "19:00",
      local: "Auditório Principal",
      cor: "bg-blue-500"
    },
    {
      id: 2,
      titulo: "Feira de Ciências",
      data: "15/06/2026",
      hora: "09:00",
      local: "Ginásio Esportivo",
      cor: "bg-green-500"
    },
    {
      id: 3,
      titulo: "Festa Junina",
      data: "22/06/2026",
      hora: "18:00",
      local: "Pátio da Escola",
      cor: "bg-orange-500"
    },
    {
      id: 4,
      titulo: "Entrega de Boletins",
      data: "28/06/2026",
      hora: "14:00",
      local: "Salas de Aula",
      cor: "bg-purple-500"
    }
  ];

  return (
    <Card className="h-fit">
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Calendar className="size-5 text-primary" />
          Próximos Eventos
        </CardTitle>
        <CardDescription>Agenda escolar</CardDescription>
      </CardHeader>
      <CardContent>
        <div className="space-y-3">
          {eventos.map((evento) => (
            <div
              key={evento.id}
              className="flex gap-3 p-3 rounded-lg border hover:bg-accent/50 transition-colors cursor-pointer"
            >
              <div className={`size-12 rounded-lg ${evento.cor} flex flex-col items-center justify-center text-white flex-shrink-0`}>
                <span className="text-xs font-medium">{evento.data.split('/')[0]}</span>
                <span className="text-xs opacity-90">{evento.data.split('/')[1]}</span>
              </div>
              <div className="flex-1 min-w-0">
                <h4 className="font-medium text-sm mb-1">{evento.titulo}</h4>
                <div className="space-y-1 text-xs text-muted-foreground">
                  <div className="flex items-center gap-1">
                    <Clock className="size-3" />
                    <span>{evento.hora}</span>
                  </div>
                  <div className="flex items-center gap-1">
                    <MapPin className="size-3" />
                    <span className="truncate">{evento.local}</span>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
        <Button variant="outline" className="w-full mt-4" size="sm">
          Ver Calendário Completo
        </Button>
      </CardContent>
    </Card>
  );
}
