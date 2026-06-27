import { FileText, Calendar, GraduationCap, BookOpen, Users, Phone, Download, MessageCircle } from "lucide-react";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "./ui/card";

export function AcessoRapido() {
  const acessos = [
    { nome: "Calendário Escolar", icone: Calendar, cor: "text-blue-600" },
    { nome: "Boletim Online", icone: FileText, cor: "text-green-600" },
    { nome: "Material Didático", icone: BookOpen, cor: "text-purple-600" },
    { nome: "Biblioteca Digital", icone: GraduationCap, cor: "text-orange-600" },
    { nome: "Fale Conosco", icone: MessageCircle, cor: "text-pink-600" },
    { nome: "Documentos", icone: Download, cor: "text-indigo-600" },
    { nome: "Equipe Escolar", icone: Users, cor: "text-teal-600" },
    { nome: "Contatos", icone: Phone, cor: "text-red-600" }
  ];

  return (
    <Card>
      <CardHeader>
        <CardTitle>Acesso Rápido</CardTitle>
        <CardDescription>Serviços e recursos disponíveis</CardDescription>
      </CardHeader>
      <CardContent>
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
          {acessos.map((acesso) => {
            const Icon = acesso.icone;
            return (
              <button
                key={acesso.nome}
                className="flex flex-col items-center gap-2 p-4 rounded-lg border hover:bg-accent hover:border-primary/50 transition-all group"
              >
                <div className={`p-3 rounded-lg bg-accent ${acesso.cor}`}>
                  <Icon className="size-6" />
                </div>
                <span className="text-xs text-center font-medium group-hover:text-primary transition-colors">
                  {acesso.nome}
                </span>
              </button>
            );
          })}
        </div>
      </CardContent>
    </Card>
  );
}
