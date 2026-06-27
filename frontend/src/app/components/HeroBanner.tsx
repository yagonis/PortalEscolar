import { ChevronLeft, ChevronRight } from "lucide-react";
import { Button } from "./ui/button";
import { useState, useEffect } from "react";

export function HeroBanner() {
  const [currentSlide, setCurrentSlide] = useState(0);

  const destaques = [
    {
      titulo: "Reunião de Pais - 1º Bimestre",
      descricao: "Participe da reunião para acompanhar o desenvolvimento dos alunos",
      data: "30 de Maio, 2026",
      imagem: "bg-gradient-to-r from-blue-600 to-blue-400"
    },
    {
      titulo: "Inscrições Abertas - Oficinas de Férias",
      descricao: "Atividades educativas e recreativas para o período de recesso",
      data: "Vagas Limitadas",
      imagem: "bg-gradient-to-r from-green-600 to-green-400"
    },
    {
      titulo: "Calendário Escolar 2026 Disponível",
      descricao: "Confira todas as datas importantes do ano letivo",
      data: "Acesse agora",
      imagem: "bg-gradient-to-r from-purple-600 to-purple-400"
    }
  ];

  const nextSlide = () => {
    setCurrentSlide((prev) => (prev + 1) % destaques.length);
  };

  const prevSlide = () => {
    setCurrentSlide((prev) => (prev - 1 + destaques.length) % destaques.length);
  };

  const goToSlide = (index: number) => {
    setCurrentSlide(index);
  };

  useEffect(() => {
    const timer = setInterval(nextSlide, 5000);
    return () => clearInterval(timer);
  }, []);

  return (
    <div className="bg-muted/30 border-b">
      <div className="container mx-auto px-4 py-8">
        <div className="relative rounded-xl overflow-hidden">
          {/* Banner Principal */}
          <div className="relative">
            {destaques.map((destaque, index) => (
              <div
                key={index}
                className={`${destaque.imagem} text-white p-8 lg:p-12 min-h-[300px] flex flex-col justify-end transition-opacity duration-500 ${
                  index === currentSlide ? 'opacity-100' : 'opacity-0 absolute inset-0'
                }`}
              >
                <div className="max-w-2xl">
                  <p className="text-sm opacity-90 mb-2">{destaque.data}</p>
                  <h2 className="text-3xl lg:text-4xl font-bold mb-3">{destaque.titulo}</h2>
                  <p className="text-lg opacity-95 mb-4">{destaque.descricao}</p>
                  <Button variant="secondary" size="lg">Saiba Mais</Button>
                </div>
              </div>
            ))}
          </div>

          {/* Controles de Navegação */}
          <div className="absolute top-1/2 -translate-y-1/2 left-4 right-4 flex justify-between pointer-events-none">
            <Button
              size="icon"
              variant="secondary"
              className="pointer-events-auto rounded-full"
              onClick={prevSlide}
            >
              <ChevronLeft className="size-5" />
            </Button>
            <Button
              size="icon"
              variant="secondary"
              className="pointer-events-auto rounded-full"
              onClick={nextSlide}
            >
              <ChevronRight className="size-5" />
            </Button>
          </div>

          {/* Indicadores */}
          <div className="absolute bottom-4 left-1/2 -translate-x-1/2 flex gap-2">
            {destaques.map((_, index) => (
              <button
                key={index}
                onClick={() => goToSlide(index)}
                className={`h-2 rounded-full transition-all ${
                  index === currentSlide ? 'w-8 bg-white' : 'w-2 bg-white/50'
                }`}
                aria-label={`Ir para slide ${index + 1}`}
              />
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
