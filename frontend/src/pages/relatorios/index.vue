<script lang="ts" setup>
import {FileDown, FileSpreadsheet, FileText} from 'lucide-vue-next'
import {Button} from '@/components/ui/button'
import {Card, CardContent, CardDescription, CardHeader, CardTitle} from '@/components/ui/card'
</script>
<template>
  <div class="space-y-6">
    <div><p class="text-sm font-medium text-primary">Administração</p>
      <h1 class="text-3xl font-bold tracking-tight">Relatórios</h1>
      <p class="mt-1 text-sm text-muted-foreground">Exporte dados do almoxarifado para análise e prestação de
        contas.</p></div>
    <div class="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
      <Card>
        <CardHeader><span class="mb-2 grid size-10 place-items-center rounded-lg bg-primary/10 text-primary"><FileDown/></span>
          <CardTitle>Estoque completo</CardTitle>
          <CardDescription>Relação atual de todos os itens cadastrados.</CardDescription>
        </CardHeader>
        <CardContent class="flex gap-2">
          <Button as-child class="flex-1">
            <RouterLink to="/relatorios/visualizar?key=estoque&format=pdf">
              <FileText/>
              PDF
            </RouterLink>
          </Button>
          <Button as-child class="flex-1" variant="outline">
            <RouterLink to="/relatorios/visualizar?key=estoque&format=excel">
              <FileSpreadsheet/>
              Excel
            </RouterLink>
          </Button>
        </CardContent>
      </Card>
    </div>
  </div>
</template>
