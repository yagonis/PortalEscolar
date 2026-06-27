import { MessageSquare, ThumbsUp, Send } from "lucide-react";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "./ui/card";
import { Button } from "./ui/button";
import { Textarea } from "./ui/textarea";

export function ParticipacaoCidada() {
  const enquetes = [
    {
      id: 1,
      pergunta: "Qual horário você prefere para as reuniões de pais?",
      opcoes: [
        { texto: "Manhã (8h-12h)", votos: 45 },
        { texto: "Tarde (14h-18h)", votos: 32 },
        { texto: "Noite (19h-21h)", votos: 78 }
      ]
    }
  ];

  const totalVotos = enquetes[0].opcoes.reduce((sum, op) => sum + op.votos, 0);

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <ThumbsUp className="size-5 text-primary" />
          Participação Cidadã
        </CardTitle>
        <CardDescription>Sua opinião é importante para nós</CardDescription>
      </CardHeader>
      <CardContent className="space-y-6">
        {/* Enquete Ativa */}
        <div>
          <h3 className="font-medium mb-3">{enquetes[0].pergunta}</h3>
          <div className="space-y-2">
            {enquetes[0].opcoes.map((opcao, index) => {
              const porcentagem = ((opcao.votos / totalVotos) * 100).toFixed(0);
              return (
                <button
                  key={index}
                  className="w-full text-left p-3 rounded-lg border hover:border-primary/50 transition-all group"
                >
                  <div className="flex justify-between items-center mb-1">
                    <span className="text-sm font-medium group-hover:text-primary">
                      {opcao.texto}
                    </span>
                    <span className="text-sm text-muted-foreground">{porcentagem}%</span>
                  </div>
                  <div className="h-2 bg-muted rounded-full overflow-hidden">
                    <div
                      className="h-full bg-primary transition-all"
                      style={{ width: `${porcentagem}%` }}
                    />
                  </div>
                </button>
              );
            })}
          </div>
          <p className="text-xs text-muted-foreground mt-2">{totalVotos} votos registrados</p>
        </div>

        {/* Caixa de Sugestões */}
        <div className="pt-4 border-t">
          <h3 className="font-medium mb-3 flex items-center gap-2">
            <MessageSquare className="size-4" />
            Envie sua sugestão
          </h3>
          <div className="space-y-2">
            <Textarea
              placeholder="Compartilhe suas ideias e sugestões para melhorar nossa escola..."
              className="resize-none"
              rows={3}
            />
            <Button className="w-full">
              <Send className="size-4 mr-2" />
              Enviar Sugestão
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>
  );
}
