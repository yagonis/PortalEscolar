import { HeroBanner } from "../components/HeroBanner";
import { AvisosRecentes } from "../components/AvisosRecentes";
import { ProximosEventos } from "../components/ProximosEventos";
import { UltimasNoticias } from "../components/UltimasNoticias";
import { AcessoRapido } from "../components/AcessoRapido";
import { ParticipacaoCidada } from "../components/ParticipacaoCidada";

export function HomePage() {
  return (
    <>
      <HeroBanner />
      <div className="container mx-auto px-4 py-8 space-y-12">
        <div className="grid lg:grid-cols-3 gap-6">
          <div className="lg:col-span-3">
            <AvisosRecentes />
          </div>
        </div>
        <UltimasNoticias />
        <ParticipacaoCidada />
      </div>
    </>
  );
}
