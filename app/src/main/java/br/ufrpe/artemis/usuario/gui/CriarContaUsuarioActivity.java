package br.ufrpe.artemis.usuario.gui;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.ufrpe.artemis.endereco.dominio.Endereco;
import br.ufrpe.artemis.infra.Auxiliar;
import br.ufrpe.artemis.infra.HttpDataHandler;
import br.ufrpe.artemis.infra.criptografia.Criptografia;
import br.ufrpe.artemis.pessoa.dominio.Pessoa;
import br.ufrpe.artemis.R;
import br.ufrpe.artemis.usuario.dominio.Usuario;
import br.ufrpe.artemis.usuario.negocio.UsuarioNegocio;

public class CriarContaUsuarioActivity extends AppCompatActivity {
    private EditText nomeRegistro;
    private EditText cpfRegistro;
    private EditText emailRegistro;
    private EditText senhaRegistro;
    private EditText confirmaSenhaRegistro;
    private EditText telefoneRegistro;
    private EditText ruaRegistro;
    private EditText numeroRegistro;
    private EditText cidadeRegistro;
    private Button botaoRegistrar;
    private boolean error = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_conta_usuario);
        setView();
        botaoRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarCadastro();
            }
        });

    }

    private void setView(){
        nomeRegistro = findViewById(R.id.nomeUsuarioId);
        cpfRegistro = findViewById(R.id.cpfId);
        emailRegistro = findViewById(R.id.pessoaEmailId);
        senhaRegistro = findViewById(R.id.senhaRegistroId);
        confirmaSenhaRegistro = findViewById(R.id.confirmarSenhaRegistroId);
        botaoRegistrar = findViewById(R.id.btAlterarId);
        telefoneRegistro = findViewById(R.id.telefoneId);
        ruaRegistro = findViewById(R.id.ruaEnderecoId);
        numeroRegistro = findViewById(R.id.numEnderecoId);
        cidadeRegistro = findViewById(R.id.cidadeEnderecoId);
    }

    private void validarCadastro() {
        if (validarCampos()) {
            criarConta();
        }
    }

    private boolean validarCampos(){
        boolean erro = true;
        if(validarCpf()){
            erro = false;
        }
        if(validarNome()){
            erro = false;
        }
        if(validarEmail()){
            erro = false;
        }
        if(validarSenha()){
            erro = false;
        }
        if(validarTelefone()){
            erro= false;
        }
        if(validarRua()){
            erro = false;
        }
        if(validarNumero()){
            erro = false;
        }
        if(validarCidade()){
            erro= false;
        }
        return erro;
    }

    private boolean validarTelefone(){
        boolean erro = false;
        String telefone = telefoneRegistro.getText().toString().trim();
        if(telefone.isEmpty()){
            erro = true;
            telefoneRegistro.setError("Campo em branco");
        }
        return erro;
    }

    private boolean validarRua(){
        boolean erro = false;
        String rua = ruaRegistro.getText().toString().trim();
        if(rua.isEmpty()){
            erro = true;
            ruaRegistro.setError("Campo em branco");
        }
        return erro;
    }

    private boolean validarNumero(){
        boolean erro = false;
        String numero = numeroRegistro.getText().toString().trim();
        if(numero.isEmpty()){
            erro = true;
            numeroRegistro.setError("Campo em branco");
        }
        return erro;
    }

    private boolean validarCidade(){
        boolean erro = false;
        String cidade = cidadeRegistro.getText().toString().trim();
        if(cidade.isEmpty()){
            erro = true;
            cidadeRegistro.setError("Campo em branco");
        }
        return erro;
    }


    private boolean validarCpf(){
        boolean erro = false;
        String cpf = cpfRegistro.getText().toString().trim();
        if(cpf.isEmpty()){
            erro = true;
            cpfRegistro.setError("Campo em branco");
        }else if(cpf.length() != 11){
            erro = true;
            cpfRegistro.setError("Cpf inválido");
        }
        return erro;
    }

    private boolean validarNome(){
        boolean erro = false;
        String nome = nomeRegistro.getText().toString().trim();
        if(nome.isEmpty()){
            erro = true;
            nomeRegistro.setError("Campo em branco");
        }
        return erro;
    }

    private boolean validarEmail(){
        boolean erro = false;
        String email = emailRegistro.getText().toString().trim();
        if(email.isEmpty()){
            erro = true;
            emailRegistro.setError("Campo em branco");
        }
        return erro;
    }

    private boolean validarSenha(){
        boolean erro = false;
        String senha = senhaRegistro.getText().toString();
        String confirmarSenha = confirmaSenhaRegistro.getText().toString();
        if(senha.isEmpty() && confirmarSenha.isEmpty()){
            erro = true;
            senhaRegistro.setError("Campo em branco");
            confirmaSenhaRegistro.setError("Campo em branco");
        } else if(senha.isEmpty()){
            erro = true;
            senhaRegistro.setError("Campo em branco");
        }else if(senha.length() < 6){
            erro = true;
            senhaRegistro.setError("A senha deve conter pelo menos 6 caracteres");
        }else if(!senha.equals(confirmarSenha)){
            erro = true;
            senhaRegistro.setError("As senhas devem ser iguais");
            confirmaSenhaRegistro.setError("As senhas devem ser iguais");
        }
        return erro;
    }

    private void criarConta(){
        UsuarioNegocio negocio = new UsuarioNegocio();
        Usuario usuario = new Usuario();
        String cpf = cpfRegistro.getText().toString().trim();
        String senha = senhaRegistro.getText().toString();
        usuario.setSenha(senha);
        usuario.setCpf(cpf);
        if(negocio.existeUsuario(cpf)){
            Toast.makeText(this, "Cpf já registrado", Toast.LENGTH_SHORT).show();
        }else{
            String ruaS = ruaRegistro.getText().toString().trim();
            String numeroS = numeroRegistro.getText().toString().trim();
            String cidadeS = cidadeRegistro.getText().toString().trim();
            String end = ruaS.replace(" ", "+") + "+" + numeroS + "+" + cidadeS.replace(" ", "+");
            new GetCoordinates().execute(end);
        }
    }

    private void inserirUsuario(double lat, double lng){
        if(error){
            Toast.makeText(this, "endereço não localizado", Toast.LENGTH_SHORT).show();
            return;
        }
        String cpf = cpfRegistro.getText().toString().trim();
        String senha = senhaRegistro.getText().toString();
        String nome = nomeRegistro.getText().toString().trim();
        String email = emailRegistro.getText().toString().trim();
        String telefone = telefoneRegistro.getText().toString().trim();
        String ruaS = ruaRegistro.getText().toString().trim();
        String numeroS = numeroRegistro.getText().toString().trim();
        String cidadeS = cidadeRegistro.getText().toString().trim();
        Usuario usuario = new Usuario();
        usuario.setCpf(cpf);
        Criptografia criptografia = new Criptografia();
        String senhaCriptografada = criptografia.criptografarString(senha);
        usuario.setSenha(senhaCriptografada);
        Pessoa pessoa = new Pessoa();
        pessoa.setNome(nome);
        pessoa.setEmail(email);
        pessoa.setTelefone(telefone);
        pessoa.setFotoPerfil(Auxiliar.comprimirImagem(Auxiliar.gerarBitmapPadrao()));
        Endereco endereco = new Endereco();
        endereco.setCidade(cidadeS);
        endereco.setRua(ruaS);
        endereco.setNumero(numeroS);
        endereco.setLat(lat);
        endereco.setLng(lng);
        UsuarioNegocio negocio = new UsuarioNegocio();
        negocio.inserirUsuario(usuario, pessoa, endereco);
        Toast.makeText(CriarContaUsuarioActivity.this, "Usuario registrado", Toast.LENGTH_SHORT).show();
        CriarContaUsuarioActivity.this.finish();
    }

    private class GetCoordinates extends AsyncTask<String,Void,String> {
        ProgressDialog dialog = new ProgressDialog(CriarContaUsuarioActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait....");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String response;
            try{
                String address = strings[0];
                HttpDataHandler http = new HttpDataHandler();
                String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=AIzaSyCI1NeacBsowhHvQTPcCLV9bZ5aUgJBm8M",address);
                response = http.getHTTPData(url);
                return response;
            }
            catch (Exception ex) {}
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonObject = new JSONObject(s);
                String ok = (String) jsonObject.get("status");
                if(ok.equals("OK")) {
                    double lat = (double) ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                            .getJSONObject("location").get("lat");
                    double lng = (double) ((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                            .getJSONObject("location").get("lng");
                    if(dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    inserirUsuario(lat, lng);
                }else{
                    error = true;
                }




            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}