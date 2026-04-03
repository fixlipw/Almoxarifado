create or replace view public.vw_pedidos_com_atrasado as
select
  p.*,
  (
    p.finalizado = false
    and p.data_aprovacao is not null
    and current_date = p.data_aprovacao::date
    and current_time > time '18:00:00'
  ) as atrasado
from public.pedidos p;